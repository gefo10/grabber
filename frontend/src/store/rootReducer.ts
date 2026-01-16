import { combineReducers } from 'redux';
import messageReducer from './slices/messageSlice';
import productReducer from './slices/productSlice';

const rootReducer = combineReducers({
    messages: messageReducer,
    products: productReducer,
});


export default rootReducer;


