package com.taxifleet.enums;

public enum BookingStrategy {
    NEAR_BY {
        @Override
        public <T> T accept(BookingStrategyVisitor<T> visitor) {
            return visitor.visitNearBy();
        }
    },
    ALL_AREA {
        @Override
        public <T> T accept(BookingStrategyVisitor<T> visitor) {
            return visitor.visitAllArea();
        }
    };


    public interface BookingStrategyVisitor<T> {
        T visitNearBy();

        T visitAllArea();
    }

    public abstract <T> T accept(BookingStrategy.BookingStrategyVisitor<T> visitor);
}
