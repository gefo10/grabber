import axios from 'axios';
import { ProductResponse, ProductQueryParams} from '@/types/Product';

const API_URL = "http://localhost:8080/api/v1/products";
export const fetchProducts = async (params?: ProductQueryParams): Promise<ProductResponse> => {
    const response = await axios.get<ProductResponse>(API_URL, {
        params: params
    });
    return response.data;
}
