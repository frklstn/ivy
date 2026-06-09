/**
 * Utility functions for FinanceApp
 */

/**
 * Converts a Kotlin ARGB integer color to a standard CSS hex string (#RRGGBB)
 */
export function intToHex(colorInt: number): string {
  // Handle signed integer shift
  const hex = (colorInt >>> 0).toString(16).padStart(8, '0');
  // Return #RRGGBB (taking the last 6 characters)
  return '#' + hex.substring(2);
}

/**
 * Converts a CSS hex string (#RRGGBB) to a signed 32-bit integer for Kotlin compatibility
 */
export function hexToInt(hex: string): number {
  let cleanHex = hex.replace('#', '');
  if (cleanHex.length === 6) {
    cleanHex = 'ff' + cleanHex; // Default full opacity (Alpha = FF)
  }
  const intVal = parseInt(cleanHex, 16);
  // Convert to signed 32-bit integer
  return intVal > 0x7fffffff ? intVal - 0x100000000 : intVal;
}

/**
 * Formats a number to currency string (default IDR)
 */
export function formatCurrency(amount: number, currency: string = 'IDR'): string {
  if (currency === 'IDR') {
    return new Intl.NumberFormat('id-ID', {
      style: 'currency',
      currency: 'IDR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(amount);
  }
  
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: currency,
  }).format(amount);
}

/**
 * List of standard premium colors for HSL selection in UI
 */
export const PREMIUM_COLORS = [
  { name: 'Ivy Green', hex: '#10b981', int: hexToInt('#10b981') },
  { name: 'Vibrant Blue', hex: '#3b82f6', int: hexToInt('#3b82f6') },
  { name: 'Soft Purple', hex: '#8b5cf6', int: hexToInt('#8b5cf6') },
  { name: 'Sunset Pink', hex: '#ec4899', int: hexToInt('#ec4899') },
  { name: 'Crimson Red', hex: '#ef4444', int: hexToInt('#ef4444') },
  { name: 'Bright Orange', hex: '#f97316', int: hexToInt('#f97316') },
  { name: 'Warm Gold', hex: '#eab308', int: hexToInt('#eab308') },
  { name: 'Teal Blue', hex: '#14b8a6', int: hexToInt('#14b8a6') },
];
