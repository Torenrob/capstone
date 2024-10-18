import axios, {AxiosResponse} from "axios";
import {ErrorHandler} from "../../Helpers/ErrorHandler";
import {RegisterUserInfo, UserProfile, UserProfile_BankAccounts} from "../../Types/APIDataTypes";

const api = import.meta.env.VITE_API_URL + "/user";

export const userLoginAPI = async (username: string, password: string) => {
	try {
		return await axios.post<UserProfile_BankAccounts>(api + "/login", {
			username: username,
			password: password,
		});
	} catch (err) {
		ErrorHandler(err);
	}
};

export const userRegisterAPI = async (registerUser: RegisterUserInfo): Promise<AxiosResponse<UserProfile> | void> => {
	try {
		return await axios.post<UserProfile>(api + "/register", registerUser);
	} catch (err) {
		ErrorHandler(err);
	}
};
