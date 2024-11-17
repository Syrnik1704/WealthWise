export interface MenuItem {
  route?: string;
  fragment?: string;
  label: string;
  icon?: string;
  action?: () => void;
}
