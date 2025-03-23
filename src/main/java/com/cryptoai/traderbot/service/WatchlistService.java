package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.model.Coin;
import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.model.Watchlist;

public interface WatchlistService {

    Watchlist findUserWatchlist(Long userId) throws Exception;
    Watchlist createWatchlist(User user);
    Watchlist findById(Long id) throws Exception;

    Coin addItemToWatchlist(Coin coin, User user) throws Exception;
    Coin removeItemFromWatchlist(Coin coin, User user) throws Exception;
}
