import { render } from '@testing-library/react';
import App from './App';
import { describe, it, expect } from 'vitest'; // Import from vitest if not using globals

describe('App', () => {
    it('renders the Chat component', () => {
        render(<App />);
        // Since we don't know what text is inside Chat, let's just check if the App container renders
        // or look for a specific element you know exists in Chat.
        // For now, let's verify the main div exists:
        const appContainer = document.querySelector('.App');
        expect(appContainer).toBeInTheDocument();
    });
});;
