import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Product, ProductQueryParams, ProductResponse } from '@/types';
import productService from '@/services/product.service';


interface ProductState {
    items: Product[];
    totalElements: number;
    totalPages: number;
    currentPage: number;
    status: 'idle' | 'loading' | 'succeeded' | 'failed';
}

const initialState: ProductState = {
    items: [],
    totalElements: 0,
    totalPages: 0,
    currentPage: 0,
    status: 'idle',
};

export const loadProducts = createAsyncThunk<ProductResponse, ProductQueryParams>(
    'products/load',
    async (params) => {
        const response = productService.fetchProducts(params);
        return response;
    }
);


const productSlice = createSlice({
    name: 'productResponse',
    initialState,
    reducers: {
        resetProducts: (state) => {
            state.items = [];
            state.status = 'idle';
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(loadProducts.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(loadProducts.fulfilled, (state, action: PayloadAction<ProductResponse>) => {
                state.status = 'succeeded';
                state.items = action.payload.content;
                state.totalElements = action.payload.totalElements;
                state.totalPages = action.payload.totalPages;
                state.currentPage = action.payload.pageNumber;
            })
            .addCase(loadProducts.rejected, (state) => {
                state.status = 'failed';
            })
    },
});

export const { resetProducts } = productSlice.actions;
export default productSlice.reducer;

