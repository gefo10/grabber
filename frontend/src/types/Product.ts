export interface Product {
    productId: string;
    productName: string;
    image: string;
    description: string;
    quantity: number;
    price: number;
    discount: number;
    specialPrice: number;
}


export interface ProductQueryParams {
    pageNumber?: number,
    pageSize?: number;
    sortBy?: string;
    sortOrder?: 'ASC' | 'DESC';
    category?: number;
    minPrice?: number;
    maxPrice?: number;
}

export interface ProductResponse {
    content: Product[],
    pageNumber: number,
    pageSize: number,
    totalElements: number;
    totalPages: number;
    last: boolean;
}
