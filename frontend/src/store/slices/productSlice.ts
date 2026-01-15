import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Product } from '@/types/Product';
import { fetchProducts } from '@/services/product.service';

interface ProductState {
    items: Product[],
    status: 'idle' | 'loading' | 'failed';
}

const initialState: ProductState = {
    items: [],
    status: 'idle',
};

export const loadProducts = createAsyncThunk(
    'products/load',
    async () => {
        const products = fetchProducts();
        return products;
    }
);


const productSlice = createSlice({
    name: 'products',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(loadProducts.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(loadProducts.fulfilled, (state, action: PayloadAction<Product[]>) => {
                state.status = 'idle';
                state.items = action.payload;
            })
            .addCase(loadProducts.rejected, (state) => {
                state.status = 'failed';
            });
    },
});

export default productSlice.reducer;
