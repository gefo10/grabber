import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth/';

export const register = async (username: string, email: string, password: string) => {
    return axios.post(API_URL + 'signup', {
        username,
        email,
        password
    });
}

export const login = async (username: string, password: string) => {
    return axios.post(API_URL + 'signin', {
        username,
        password
    }).then((response) => {
        if (response.data.accessToken) {
            localStorage.setItem('user', JSON.stringify(response.data));
        }
        return response.data;
    });
}

export const logout = () => {
    localStorage.removeItem('user');
}

export const getCurrentUser = () => {
    const user = localStorage.getItem('user');
    if (user) {
        return JSON.parse(user);
    }
    return null;
}
