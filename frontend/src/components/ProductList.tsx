import React, { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { loadProducts } from '@/store/slices/productSlice';
import { useNavigate, useLocation } from 'react-router-dom';
import { Product } from '@/types';

const ProductList: React.FC = () => {
    const dispatch = useAppDispatch();
    const navigate = useNavigate();
    const location = useLocation();

    const { items, status, currentPage, totalElements, totalPages } = useAppSelector((state) => state.productResponse);
    const { token } = useAppSelector((state) => state.auth)
    const isAuthenticated = !!token;

    console.log("Current Page:" + currentPage);
    console.log("Total Pages:" + totalPages);
    console.log("Total Elements:" + totalElements);

    useEffect(() => {
        if (status == 'idle') {
            dispatch(loadProducts({
                pageNumber: 0,
                pageSize: 12
            }));
        }
    }, [dispatch, status]);

    const handleAddToCart = async (product: Product) => {
        if (!isAuthenticated) {
            navigate('/login', { state: { from: location } });
            return;
        }

        //dispatch(addToCartAsync(

    }
    if (status === 'loading') return <p>Loading products...</p>;
    if (status === 'failed') return <p>Error loading products...</p>;
    return (
        <div className="product-list">
            <h2>Our Products</h2>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '20px' }}>
                {items.map((product) => (
                    <div key={product.productId} style={{ border: '1px solid #ccc', padding: '10px', borderRadius: '8px' }}>
                        {/* Placeholder image if none provided */}
                        <img
                            src={product.image || 'https://placehold.co/150'}
                            alt={product.productName}
                            style={{ width: '100%', height: '150px', objectFit: 'cover' }}
                        />
                        <h3>{product.productName}</h3>
                        <p>{product.description}</p>
                        <p><strong>${product.price}</strong></p>
                        <button>Add to Cart</button>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ProductList;
