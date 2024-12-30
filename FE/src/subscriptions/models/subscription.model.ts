export interface Subscription {
    subscriptionId: number;
    subscriptionTitle: string;
    amount: number;
    cyclicalPaymentDate: number;
    description?: string;
    creationDate: Date;
}

export type SubscriptionRequest = Omit<Subscription, 'subscriptionId'>;