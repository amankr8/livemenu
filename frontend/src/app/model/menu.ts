// menu.component.ts
export interface MenuItem {
  id: number;
  name: string;
  price: number;
  description: string;
  category: string;
  image: string;
  isVeg: boolean;
}

export const MOCK_MENU: MenuItem[] = [
  {
    id: 1,
    name: 'Butter Chicken Biryani',
    price: 320,
    description:
      'Creamy butter chicken layered with fragrant basmati rice and aromatic spices.',
    category: 'Main Course',
    image:
      'https://images.unsplash.com/photo-1563379091339-03b21bc4a4f8?q=80&w=500',
    isVeg: false,
  },
  {
    id: 2,
    name: 'Paneer Tikka Platter',
    price: 280,
    description:
      'Fresh cottage cheese marinated in yogurt and grilled to perfection.',
    category: 'Starters',
    image:
      'https://images.unsplash.com/photo-1567184109411-47a7a394853a?q=80&w=500',
    isVeg: true,
  },
  {
    id: 3,
    name: 'Garlic Butter Naan',
    price: 60,
    description:
      'Soft, leavened bread brushed with fresh garlic and melted butter.',
    category: 'Breads',
    image:
      'https://images.unsplash.com/photo-1601050690597-df0568f70950?q=80&w=500',
    isVeg: true,
  },
];
