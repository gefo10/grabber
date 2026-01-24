import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { LoginCredentials, User } from "@/types";
import authService from "@/services/auth.service";
import { AxiosError } from "axios";
interface AuthState {
    user: User | null;
    token: string | null;
    isLoading: boolean;
    error: string | null;
}

const storedToken = localStorage.getItem('token');
const storedUser = localStorage.getItem('user');

const initialState: AuthState = {
    user: storedUser ? JSON.parse(storedUser) : null,
    token: storedToken || null,
    isLoading: false,
    error: null,
};


const loginUser = createAsyncThunk(
    'auth/login',
    async (credentials: LoginCredentials, thunkApi) => {
        try {
            return await authService.login(credentials);
        } catch (error) {
            const err = error as AxiosError<{ message: string }>;
            const errorMessage = err.response?.data?.message || 'Login failed';
            return thunkApi.rejectWithValue(errorMessage);
        }
    }
);


const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        logout: (state) => {
            state.user = null;
            state.token = null;
            state.isLoading = false;
            state.error = null;
            localStorage.removeItem('token');
            localStorage.removeItem('user');
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(loginUser.pending, (state) => {
                state.isLoading = true;
                state.error = null;
            })
            .addCase(loginUser.fulfilled, (state, action) => {
                state.isLoading = false;
                state.error = null;
                state.user = action.payload.user;
                state.token = action.payload.token;

                localStorage.setItem('token', action.payload.token);
                localStorage.setItem('user', JSON.stringify(action.payload.user));
            })
            .addCase(loginUser.rejected, (state, action) => {
                state.user = null;
                state.token = null;
                state.isLoading = false;
                state.error = action.payload as string;
            });
    }
});

export const { logout } = authSlice.actions;
export default authSlice.reducer;

