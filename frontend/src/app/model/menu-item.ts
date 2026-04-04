export type FoodPreference = 'all' | 'veg' | 'non-veg';

export interface MenuItem {
  id: number;
  name: string;
  desc: string;
  imageUrl: string;
  categoryId: number;
  inStock: boolean;
  isVeg: boolean;
  price: number;
}
