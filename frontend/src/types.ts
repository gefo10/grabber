/*
    *
    * Auth types
*
*/

export interface AuthResponse {
    token: string;
    user: User;
}

export interface LoginCredentials {
    email: string;
    password: string;
}

export interface RegisterUserRequest {
    firstName: string;
    lastName: string;
    email: string;
    pasword: string;
    addresses?: Address[];
}

//DTO for messages in chat (not supported for now)
export interface ChatMessage {
    id: number;
    message: string;
    sender: string;
    timestamp: string;
}

/***
    * Product types
    **/
//ProductDTO response expected from backend
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


//Query params for requesting products (uses Pageable)
export interface ProductQueryParams {
    pageNumber?: number,
    pageSize?: number;
    sortBy?: string;
    sortOrder?: 'ASC' | 'DESC';
    category?: number;
    minPrice?: number;
    maxPrice?: number;
}

//response from backend when accessing products (uses Pageable)
export interface ProductResponse {
    content: Product[],
    pageNumber: number,
    pageSize: number,
    totalElements: number;
    totalPages: number;
    last: boolean;
}

//request to add a product to the user's cart
export interface AddProductToCartRequest {
    productId: number,
    quantity: number,
}

/*
    * UserDTO types
*/
export interface Address {
    addressId: number;
    street: string;
    additionalInfo: string | null;
    city: string;
    country: string;
    postalCode: string;
    houseNumber: string;
}
export interface User {
    userId: string;
    firstName: string;
    lastName: string;
    email: string;
    mobileNumber: string;
    roles: ('CUSTOMER' | 'ADMIN')[];
    addresses: Address[],
    cart?: Cart;

}

/***
    * CartDTO types 
***/

export interface Cart {
    cartId: number;
    totalPrice: number;
    items: CartItem[];
}

export interface CartItem {
    cartItemId: number;
    product: Product;
    quantity: number;
    discount: number;
    subTotal: number;
}

