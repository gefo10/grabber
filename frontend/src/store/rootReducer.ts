import { combineReducers } from 'redux';
import productReducer from './slices/productSlice';
import authReducer from '@/features/auth/authSlice';
import cartReducer from './slices/cartSlice';

const rootReducer = combineReducers({
    products: productReducer,
    auth: authReducer,
    cart: cartReducer,
});


export default rootReducer;


