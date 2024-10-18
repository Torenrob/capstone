import {
	Button,
	DateInput,
	Input,
	Select,
	SelectItem,
	SharedSelection,
	Textarea
} from "@nextui-org/react";
import React, {ChangeEvent, forwardRef, useContext, useImperativeHandle, useMemo, useState} from "react";
import {BankAccountAPIData, PostTransactionAPIData, TransactionAPIData} from "../../Types/APIDataTypes";
import ArrowDownIcon from "../Icons/ArrowDownIcon";
import SubmitTransactionIcon from "../Icons/SubmitTransactionIcon";
import {deleteTransactionAPI, postTransactionAPI, updateTransactionAPI} from "../../Services/ApiCalls/TransactionAPI";
import InvalidSubmitIcon from "../Icons/InvalidSubmitIcon";
import DebitIcon from "../Icons/DebitIcon";
import {CalendarContext, UpdateTransactionContainerInfo} from "./CalendarCtrl";
import {ErrorHandler} from "../../Helpers/ErrorHandler";
import {closeDrawer, updateDailyBalances, updateDailyBalanceStates} from "../../Utilities/UtilityFuncs";
import CreditIcon from "../Icons/CreditIcon";
import {UserContext} from "../../Services/Auth/UserAuth";
import {AxiosResponse} from "axios";
import {parseDate} from "@internationalized/date";

export type TransactionInputDrawerRef = {
	updateContainer: (arg: UpdateTransactionContainerInfo) => void;
};

export type TransactionInputDrawerProps = {
	bankAccounts: BankAccountAPIData[];
	currentAcct: BankAccountAPIData;
	updAcctTrans: (arg0: TransactionAPIData, arg1: (arg0: BankAccountAPIData[]) => void) => void;
};

const countDecimals = function (value: string): number {
	return value.split(".")[1].length || 0;
};

export const TransactionInputDrawer = forwardRef<TransactionInputDrawerRef, TransactionInputDrawerProps>(({ bankAccounts, currentAcct, updAcctTrans }, ref) => {
	const [transactionType, setTransactionType] = useState<boolean>(true);
	const [submittingTransaction, setSubmittingTransaction] = useState<boolean>(false);
	const [errorMessage, setErrorMessage] = useState<boolean>(false);

	const { user, updBankAccounts } = useContext(UserContext);
	const [containerInfo, setContainerInfo] =
		useState<UpdateTransactionContainerInfo>({ amount: "0.00", editingExisting: false, title: "", bankAccountId: currentAcct.id.toString()! });

	useImperativeHandle(ref, () => ({
		updateContainer(arg: UpdateTransactionContainerInfo) {
			const { date: newDate, ...transactionContainerInfo } = arg;

			if (Object.keys(transactionContainerInfo).length === 0) {
				setContainerInfo({ date: arg.date, amount: "0.00", title: "", editingExisting: false });
				return;
			} else {
				setContainerInfo(arg);
				setTransactionType(arg.transactionObj ? arg.transactionObj?.transactionType === "Debit" : true);
			}
		},
	}));

	const { addTransToDate, editTransOnDatesFuncsMap, dailyBalancesMap, dateTransactionsMap, setStateDailyBalanceMap } = useContext(CalendarContext);

	async function SubmitTransaction(event: React.FormEvent<HTMLFormElement>, editingExisting: boolean) {
		setSubmittingTransaction(true);
		event.preventDefault();
		const postTransactionData = mkPostTransAPIData(event.currentTarget, transactionType, user!.id, currentAcct);
		let response;
		let editTransactionIsSameDate: boolean;
		if (containerInfo.editingExisting) {
			const updatedTrans = mkUpdTransAPIData(containerInfo.transactionObj!, postTransactionData);
			editTransactionIsSameDate = updatedTrans.date === containerInfo.transactionObj!.date;
			response = await updateTransactionAPI(updatedTrans, user!.id);
		} else {
			response = await postTransactionAPI(postTransactionData);
		}

		if (!response) {
			setTimeout(() => {
				setErrorMessage(true);
				setSubmittingTransaction(false);
				return;
			}, 500);
		} else {
			setTimeout(() => {
				const responseData: TransactionAPIData = response?.data;
				const oldContTransObj = { ...containerInfo!.transactionObj };

				const saveDate = containerInfo?.date;
				if (!addTransToDate.current && !editingExisting) {
					setErrorMessage(true);
					setSubmittingTransaction(false);
					return;
				}
				if (containerInfo.editingExisting) {
					if (!editTransactionIsSameDate) {
						containerInfo.deleteTransactionFromDate!(containerInfo.transactionObj!);
						const editTransOnDateFuncs = editTransOnDatesFuncsMap.current.get(responseData.date);
						editTransOnDateFuncs![0](responseData);
						//Next two lines update the container info, in case user tries to edit from container again without selecting a diff transaction
						containerInfo.transactionObj!.date = responseData.date;
						containerInfo.deleteTransactionFromDate = editTransOnDateFuncs![1];
					} else {
						containerInfo.editTransactionFunc!(responseData);
					}
				} else {
					if (responseData.bankAccountId === currentAcct.id) {
						addTransToDate.current!(responseData);
					} else {
						updAcctTrans(responseData, updBankAccounts);
					}
				}
				if (responseData.bankAccountId === currentAcct.id) {
					const dailyBalChgChk = updateDailyBalances(dateTransactionsMap.current!, dailyBalancesMap.current, responseData, oldContTransObj as TransactionAPIData);
					dailyBalancesMap.current = dailyBalChgChk[0];
					dailyBalChgChk[1] ? updateDailyBalanceStates(setStateDailyBalanceMap.current, dailyBalancesMap.current) : null;
				}
				const form: HTMLFormElement = document.querySelector(".transactionForm") as HTMLFormElement;
				form.reset();
				setContainerInfo({ ...containerInfo, date: saveDate, title: "", amount: "0.00" });
				// @ts-expect-error - TS complains about title not having a focus function due to it being a string, but it does
				form.title.focus();
				setSubmittingTransaction(false);
			}, 50);
		}
	}

	const validateAmount: boolean = useMemo(() => {
		if (containerInfo.amount?.includes(".")) {
			if (countDecimals(containerInfo?.amount) > 2) return true;
		}
		if (containerInfo?.amount?.startsWith("0") && containerInfo.amount.length > 2) {
			if (!containerInfo?.amount?.match(/^0\d?\./g)) return true;
		}
		return Number(containerInfo?.amount) < 0;
		
	}, [containerInfo?.amount]);

	function transactionTypeClick(event: React.MouseEvent<HTMLButtonElement>) {
		const target = event.currentTarget;
		if (target.name === "debitBtn") {
			setTransactionType(true);
		} else {
			setTransactionType(false);
		}
	}

	async function deleteTransaction() {
		const resp: AxiosResponse<string> | null = await deleteTransactionAPI(containerInfo.id!);
		if (resp?.status === 200) {
			containerInfo.deleteTransactionFromDate!(containerInfo.transactionObj!);
			const updBalanceMap = updateDailyBalances(dateTransactionsMap.current!, dailyBalancesMap.current, undefined, containerInfo.transactionObj);
			updateDailyBalanceStates(setStateDailyBalanceMap.current, updBalanceMap[0]);
			closeDrawer();
		} else {
			ErrorHandler("Transaction Delete API Failed");
		}
	}

	function handleAccountSelectChange(e: SharedSelection) {
		updateExistingTransDisplay({target: {name: "account", value: e.currentKey}} as ChangeEvent<HTMLInputElement>)
	}

	function updateExistingTransDisplay(e: ChangeEvent<HTMLInputElement> | ChangeEvent<HTMLSelectElement>) {
		switch (e.target.name) {
			case "title":
				setContainerInfo({ ...containerInfo, title: e.target.value });
				break;
			case "account":
				setContainerInfo({ ...containerInfo, bankAccountId: e.target.value });
				break;
			case "category":
				setContainerInfo({ ...containerInfo, category: e.target.value });
				break;
			case "description":
				setContainerInfo({ ...containerInfo, description: e.target.value });
				break;
		}
	}

	function updateAmount(e: string) {
		if (e.length === 2) {
			e.concat(".");
		}
		setContainerInfo({ ...containerInfo, amount: e });
	}

	function clearErrorMessage() {
		setErrorMessage(false);
	}

	return (
		<div id="calendarDrawer" className="absolute transactionDrawer drawerClosed" style={{ backgroundColor: "rgba(0, 0, 0,.8)", zIndex: 2, width: "100%" }}>
			<div className="grid h-full">
				<Button
					onClick={closeDrawer}
					radius="full"
					size="sm"
					isIconOnly
					variant="light"
					className="absolute z-10 pt-2 left-[50%] -translate-x-[50%] md:left-0 md:translate-x-0 md:pt-0 md:justify-self-start">
					<ArrowDownIcon />
				</Button>
				<form className="grid grid-cols-2 gap-1 grid-rows-4 lg:grid-col-4 lg:grid-rows-2 lg:gap-3 transactionForm" onSubmit={(e) => SubmitTransaction(e, containerInfo.editingExisting)}>
					<div className="absolute w-[5vw] text-red-600 font-semibold text-sm h-full pb-6 grid content-end">
						{errorMessage && (
							<span className="text-right">
								Error Submitting Transaction <br /> Try Again
								<Button className="h-4" radius="none" color="danger" onClick={clearErrorMessage}>
									Clear
								</Button>
							</span>
						)}
					</div>
					<div className="grid grid-cols-2 gap-3 grid-rows-2 lg:col-start-1 lg:row-start-1 lg:col-span-3 lg:flex lg:gap-3">
						<Input
							required
							radius="none"
							size="sm"
							className="lg:w-3/5 col-span-2 text-slate-500 basis-3/6"
							type="text"
							label="Title"
							name="title"
							isClearable
							id="TransactionDrawerTitle"
							value={containerInfo?.title ? containerInfo.title : ""}
							onChange={updateExistingTransDisplay}
						/>
						<DateInput
							className="row-start-2 lg:col-start-2 lg:row-start-1 lg:basis-1/6"
							radius="none"
							label="Date"
							name="date"
							size="sm"
							value={containerInfo?.date ?? parseDate(new Date().toISOString().split("T")[0])}
							onChange={(e) => setContainerInfo({ ...containerInfo, date: e })}
						/>
						{currentAcct.id != 0 && <Select
							required
							// placeholder={`${currentAcct.title}`}
							radius="none"
							size="sm"
							selectedKeys={[`${currentAcct.id}`]}
							label="Account"
							name="account"
							onSelectionChange={handleAccountSelectChange}
							className="text-slate-500 basis-2/6 lg:row-start-1">
							{bankAccounts.slice(0, bankAccounts.length - 1).map((account) => (
								<SelectItem key={account.id}>{account.title}</SelectItem>
							))}
						</Select>}
					</div>
					<div className="flex gap-2 lg:col-start-1 lg:row-start-2 lg:col-span-2 lg:gap-3">
						<Button
							isIconOnly
							disabled={validateAmount || errorMessage}
							size="md"
							isLoading={submittingTransaction}
							radius="none"
							className={`self-center align-middle ${validateAmount ? "lg:mb-6" : "mb-4 lg:mb-5"} bg-[${validateAmount || errorMessage ? "#D4D4" : "#6EC4A7"}]`}
							type="submit">
							{errorMessage ? <InvalidSubmitIcon white={false} /> : validateAmount ? <InvalidSubmitIcon white={false} /> : <SubmitTransactionIcon />}
						</Button>
						<Input
							required
							value={containerInfo.amount}
							onValueChange={(e) => updateAmount(e)}
							radius="none"
							size="sm"
							isInvalid={validateAmount}
							className="text-slate-500 self-center lg:col-start-2 lg:row-start-2 lg:relative lg:-top-2"
							type="number"
							label="Amount"
							name="amount"
							errorMessage="Invalid Amount"
							isClearable
							startContent={
								<div className="flex items-center">
									{!validateAmount && <span className="text-sm">$</span>}
									{validateAmount && <span className="invalidAmount pb-1">❌</span>}
								</div>
							}
						/>
						<div className="flex gap-3 relative -translate-y-1 lg:-translate-y-1.5">
							<div className="flex flex-col">
								<label htmlFor="debitBtn" className="text-xs text-slate-300 font-semibold text-center">
									Debit
								</label>
								<Button isIconOnly radius="none" size="sm" name="debitBtn" onClick={transactionTypeClick} color={transactionType ? "danger" : "default"}>
									<DebitIcon />
								</Button>
							</div>
							<div className="flex flex-col">
								<label htmlFor="creditBtn" className="text-xs text-slate-300 font-semibold relative -translate-x-0.5">
									Credit
								</label>
								<Button isIconOnly radius="none" size="sm" name="creditBtn" onClick={transactionTypeClick} color={transactionType ? "default" : "primary"}>
									<CreditIcon />
								</Button>
							</div>
						</div>
					</div>
					<Select
						selectedKeys={containerInfo?.category ? [`${containerInfo.category}`] : ["None"]}
						radius="none"
						size="sm"
						label="Category"
						name="category"
						onChange={updateExistingTransDisplay}
						className="h-4 text-slate-500 lg:col-start-3 lg:row-start-2 ">
						{user!.categories.map((cat) => {
							return <SelectItem key={cat}>{cat}</SelectItem>;
						})}
					</Select>
					<Textarea
						radius="none"
						className="lg:col-start-4 lg:row-start-1 lg:row-span-2 mt-1"
						label="Description"
						name="description"
						onChange={updateExistingTransDisplay}
						value={containerInfo?.description ? containerInfo.description : undefined}
					/>
					{containerInfo.editingExisting && (
						<div className="delTransBtn absolute lg:flex-col lg:translate-y-2">
							<Button color="danger" radius="none" isIconOnly onClick={deleteTransaction}>
								<InvalidSubmitIcon white={true} />
							</Button>
							<div className="text-sm -translate-y-2 lg:translate-y-0 text-white">Delete</div>
						</div>
					)}
				</form>
				<Button onClick={closeDrawer} radius="full" size="sm" isIconOnly variant="light" className="absolute hidden md:flex md:justify-self-end">
					<ArrowDownIcon />
				</Button>
			</div>
		</div>
	);
});

export default TransactionInputDrawer;

function mkPostTransAPIData(targetData: EventTarget & HTMLFormElement, transactionType: boolean, userId: string, curAcct: BankAccountAPIData): PostTransactionAPIData {
	return {
		userId: userId,
		// @ts-expect-error - TS complains about title not having a value due to it being a string, but it does
		title: targetData.title.value,
		transactionType: transactionType ? 0 : 1,
		bankAccountId: targetData.account.value ? targetData.account.value : curAcct.id,
		date: targetData.date.value,
		amount: targetData.amount.value,
		category: targetData.category.value,
		description: targetData.description.value,
	};
}

function mkUpdTransAPIData(curTransInfo: TransactionAPIData, newTransInfo: PostTransactionAPIData): TransactionAPIData {
	return {
		...newTransInfo,
		transactionType: newTransInfo.transactionType === 0 ? "Debit" : "Credit",
		id: curTransInfo.id,
		repeatGroupId: curTransInfo.repeatGroupId,
	};
}
