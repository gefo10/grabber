import { combineReducers } from 'redux';
import messageReducer from './slices/messageSlice';

const rootReducer = combineReducers({
    messages: messageReducer,
});


export default rootReducer;


