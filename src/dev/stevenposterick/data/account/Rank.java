package dev.stevenposterick.data.account;

public enum Rank {
    HOST("Admin"),
    USER("User");

    private final String rank;

    Rank(String rank) {
        this.rank = rank;
    }

    public static Rank getRank(String data) {
        for (Rank rank : Rank.values()) {
            if (data.equals(rank.getRankName())){
                return rank;
            }
        }
        return Rank.USER;
    }

    public String getRankName() {
        return rank;
    }
}
