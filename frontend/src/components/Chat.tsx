import React from 'react';
import { useState } from 'react';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { useHistory } from 'react-router-dom';

export interface ChatMessage {
    id: number;
    message: string;
    sender: string;
    timestamp: string;
}

const Chat: React.FC = () => {

    
    const messages = useAppSelector((state) => state.messages);
    const dispatch = useAppDispatch();

    const [formData, setFormData] = useState({});

    //const [messages, setMessages] = useState<ChatMessage[]>([]);
    //const [message, setMessage] = useState<ChatMessage | null>(null);


    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        const dataToSubmit = { ...formData };

        history.push('/chat');
    }
    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [event.target.name]: event.target.value });
    }

    return (
        <div className="chat">
        <p>Chat</p>
            <ul id="messages">
                { messages.map((message) => (
                    <li key={message.id}>{message.user_id}: {message.content}</li>
                )) }
            </ul>
            <form action="http://localhost:8080"> 
                <input id="message-input" type="text" name="messageInput" onChange={handleInputChange} />
                <button type="submit">Send</button>
            </form>

        </div>
    )
}

export default Chat;

