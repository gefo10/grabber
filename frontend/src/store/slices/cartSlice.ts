import { createSlice, createAsyncThunk, PayloadAction } from "@reduxjs/toolkit";
import { AddProductToCartRequest, Cart, CartItem } from "@/types";
import cartService from "@/services/cart.service";
import { AxiosError } from "axios";
import api from "@/utils/api";


export const fetchCart = createAsyncThunk<Cart>(
    'cart/fetchCart',
    async (_, thunkAPI) => {
        try {
            const response = await api.get('/cart');
            return response.data;
        } catch (error: unknown) {
            if (error instanceof AxiosError) {
                return thunkAPI.rejectWithValue(error.response?.data?.message || 'Failed to load cart');
            } else {
                throw error;
            }
        }
    }
)
export const addToCartAsync = createAsyncThunk<Cart, AddProductToCartRequest>(
    'cart/addToCart',
    async (request, thunkAPI) => {
        try {
            const response = cartService.addToCart(request);
            return response;
        } catch (error: unknown
        ) {
            if (error instanceof AxiosError) {
                return thunkAPI.rejectWithValue(error.response?.data?.message || 'Failed to add to cart.');
            } else {
                // for debugging
                throw error;
            }
        }
    }
);

export const removeFromCartAsync = createAsyncThunk<Cart, number>(
    'cart/removeFromCart',
    async (cartItemId, thunkAPI) => {
        try {
            const response = await api.delete(`cart/items/${cartItemId}`);
            return response.data;
        } catch (error: unknown) {
            if (error instanceof AxiosError) {
                return thunkAPI.rejectWithValue(error.response?.data?.message) || 'Failed to remove item from cart';
            } else {
                throw error;
            }
        }
    }
)


interface CartState {
    cartId: number | null;
    items: CartItem[],
    totalPrice: number,
    status: 'idle' | 'loading' | 'failed';
    error: string | null;
}


const initialState: CartState = {
    cartId: null,
    items: [],
    totalPrice: 0,
    status: 'idle',
    error: null,
}


const cartSlice = createSlice({
    name: 'cart',
    initialState: initialState,
    reducers: {
        clearCartLocal: (state) => {
            state.items = [];
            state.totalPrice = 0;
            state.cartId = null;
            state.status = 'idle';
        },
    },
    extraReducers: (builder) => {
        builder
            // --- Fetch Cart ---
            .addCase(fetchCart.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(fetchCart.fulfilled, (state, action: PayloadAction<Cart>) => {
                state.status = 'idle';
                state.cartId = action.payload.cartId;
                state.totalPrice = action.payload.totalPrice;
                state.items = action.payload.items;
            })
            .addCase(fetchCart.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as string;
            })

            // --- Add to Cart ---
            .addCase(addToCartAsync.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(addToCartAsync.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as string;
            })
            .addCase(addToCartAsync.fulfilled, (state, action: PayloadAction<Cart>) => {
                state.status = 'idle';
                state.items = action.payload.items;
                state.cartId = action.payload.cartId;
                state.totalPrice = action.payload.totalPrice;
            })

            // --- Remove from Cart ---
            .addCase(removeFromCartAsync.fulfilled, (state, action: PayloadAction<Cart>) => {
                // fresh data from server
                state.items = action.payload.items;
                state.totalPrice = action.payload.totalPrice;
            });
    }
});

export const { clearCartLocal } = cartSlice.actions;
export default cartSlice.reducer;
