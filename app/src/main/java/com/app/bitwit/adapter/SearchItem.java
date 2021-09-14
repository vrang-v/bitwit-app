package com.app.bitwit.adapter;

import com.app.bitwit.domain.Stock;
import com.app.bitwit.util.Colors;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class SearchItem {
    
    private Stock stock;
    
    private boolean showVoteScreen;
    
    private Long voteId;
    
    public SearchItem(Stock stock, boolean showVoteScreen) {
        this.stock          = stock;
        this.showVoteScreen = showVoteScreen;
    }
    
    public static SearchItem fromStock(Stock stock) {
        return new SearchItem(stock, false);
    }
    
    public static List<SearchItem> fromStocks(List<Stock> stocks) {
        return stocks.stream( ).map(SearchItem::fromStock).collect(Collectors.toList( ));
    }
    
    public String getCurrentPriceString( ) {
        if (stock.getCurrentPrice( ) < 100) {
            return String.format(Locale.getDefault( ), "%.2f 원", stock.getCurrentPrice( ));
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(stock.getCurrentPrice( )) + " 원";
    }
    
    public String getFluctuateRateString( ) {
        return stock.getFluctuateRate24h( ) >= 0
               ? String.format(Locale.getDefault( ), "+%.2f%%", stock.getFluctuateRate24h( ))
               : String.format(Locale.getDefault( ), "%.2f%%", stock.getFluctuateRate24h( ));
    }
    
    public int getStockTextColor( ) {
        return stock.getFluctuateRate24h( ) > 0 ? Colors.PRIMARY_RED : Colors.PRIMARY_BLUE;
    }
}
