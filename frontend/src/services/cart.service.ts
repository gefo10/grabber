import api from "@/utils/api";
import { AddProductToCartRequest, Cart } from "@/types";

const cartService = {
    addToCart: async (addToCartRequest: AddProductToCartRequest): Promise<Cart> => {
        const response = await api.post('cart/items', addToCartRequest);
        return response.data;
    },
}

export default cartService;
