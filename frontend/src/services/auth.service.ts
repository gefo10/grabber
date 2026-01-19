import api from "@/utils/api";
import { LoginCredentials, AuthResponse, RegisterUserRequest } from "@/types";


const authService = {
    login: async (credentials: LoginCredentials): Promise<AuthResponse> => {
        const response = await api.post('auth/login', credentials);
        return response.data;
    },
    register: async (userData: RegisterUserRequest) => {
        const response = await api.post('auth/register', userData);
        return response.data;
    },
};

export default authService;


