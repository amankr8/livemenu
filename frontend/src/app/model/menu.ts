export interface MenuItem {
  id: number;
  name: string;
  desc: string;
  imageUrl: string;
  kitchenId: number;
  category: string;
  inStock: boolean;
  isVeg: boolean;
  price: number;
}
