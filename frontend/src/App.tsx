import './App.css';
import ProductList from './components/ProductList';

function App() {
    return (
        <div className="App">
            <header>
                <h1>My Awesome Shop</h1>
            </header>
            <main>
                <ProductList />
            </main>
        </div>
    );
}

export default App;
