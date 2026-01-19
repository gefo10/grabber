import store from "@/store/store";
import axios from "axios";
import { logout } from "@/features/auth/authSlice";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    headers: {
        'Content-Type': 'application/json'
    },
    timeout: 10_000
});

api.interceptors.request.use(
    (config) => {
        const token = store.getState().auth.token;
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);


api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status == 401) {
            store.dispatch(logout());

            //heard redirect - refresh page and redirect to login (clears sensitive data lingering in memory state - good for security)
            //Optional: could later switch to soft redirect with tsx component
            window.location.href = '/login';
        }

        if (error.response?.status == 500) {
            console.error("Critical Server Error:", error.response.data);
        }
        return Promise.reject(error);
    }
);


export default api;
