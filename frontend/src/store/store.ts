import { configureStore } from '@reduxjs/toolkit';
//import rootReducer from './rootReducer';
import messageReducer from './slices/messageSlice';
import productsReducer from './slices/productSlice';

const store = configureStore({
    reducer: {
        messages: messageReducer,
        productResponse: productsReducer,
    },

});


export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export default store;

