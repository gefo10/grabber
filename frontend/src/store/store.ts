import { configureStore } from '@reduxjs/toolkit';
//import rootReducer from './rootReducer';
import productsReducer from './slices/productSlice';
import authReducer from '@/features/auth/authSlice';

const store = configureStore({
    reducer: {
        productResponse: productsReducer,
        auth: authReducer,
    },

});


export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export default store;

