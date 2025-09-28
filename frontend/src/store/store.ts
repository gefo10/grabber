import { configureStore } from '@reduxjs/toolkit';
//import rootReducer from './rootReducer';
import messageReducer from './slices/messageSlice';

const store = configureStore({
    reducer: {
        messages: messageReducer,
    },

});


export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export default store;

