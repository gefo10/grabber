import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "../store";

interface Message {
    id: number;
    conversation_id: number;
    user_id: number;
    content: string;
}

const initialState: Message[] = [
    { id: 1, conversation_id: 1, user_id: 1, content: "Hello" },
    { id: 2, conversation_id: 1, user_id: 2, content: "Hi" },
    { id: 3, conversation_id: 2, user_id: 1, content: "World" },
    { id: 4, conversation_id: 2, user_id: 3, content: "Hi everyone" },
];


const messageSlice = createSlice({
    name: 'messages',
    initialState,
    reducers: {
        addMessage: (state, action: PayloadAction<Message>) => {
            state.push(action.payload);
        },
        removeMessage: (state, action: PayloadAction<number>) => {
            return state.filter((message) => message.id !== action.payload);
        },
    }
});


export const { addMessage, removeMessage } = messageSlice.actions;
export const selectMessages = (state: RootState) => state.messages;
export default messageSlice.reducer;

