import api from '@/utils/api';
import { ProductResponse, ProductQueryParams } from '@/types';

const productService = {
    fetchProducts: async (params?: ProductQueryParams): Promise<ProductResponse> => {
        const response = await api.get('products', {
            params: params,
        });
        return response.data;
    },
}

export default productService;
