export const isRootDomain = () => {
  const host = window.location.hostname;
  return host === 'livemenu.in' || host === 'localhost';
};
