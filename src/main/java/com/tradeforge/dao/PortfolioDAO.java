package com.tradeforge.dao;

import com.tradeforge.model.Portfolio;
import com.tradeforge.singleton.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class PortfolioDAO {
    public List<Portfolio> findByUser(int userId) throws SQLException { List<Portfolio> list=new ArrayList<>(); String sql="SELECT p.*, s.symbol, s.company_name, s.price FROM portfolio p JOIN stocks s ON s.id=p.stock_id WHERE p.user_id=? ORDER BY s.symbol"; try(Connection connection=DatabaseConnection.getInstance().getConnection(); PreparedStatement statement=connection.prepareStatement(sql)){ statement.setInt(1,userId); try(ResultSet result=statement.executeQuery()){ while(result.next()) list.add(map(result)); } } return list; }
    public Portfolio findHolding(int userId,int stockId,Connection connection) throws SQLException { try(PreparedStatement statement=connection.prepareStatement("SELECT p.*, s.symbol, s.company_name, s.price FROM portfolio p JOIN stocks s ON s.id=p.stock_id WHERE p.user_id=? AND p.stock_id=?")){ statement.setInt(1,userId); statement.setInt(2,stockId); try(ResultSet result=statement.executeQuery()){ return result.next()?map(result):null; } } }
    public void changeHolding(int userId,int stockId,int delta,Connection connection) throws SQLException { Portfolio existing=findHolding(userId,stockId,connection); if(existing==null){ if(delta>0) try(PreparedStatement statement=connection.prepareStatement("INSERT INTO portfolio(user_id,stock_id,quantity) VALUES(?,?,?)")){ statement.setInt(1,userId);statement.setInt(2,stockId);statement.setInt(3,delta);statement.executeUpdate(); } } else { int next=existing.getQuantity()+delta; if(next<=0) try(PreparedStatement statement=connection.prepareStatement("DELETE FROM portfolio WHERE id=?")){ statement.setInt(1,existing.getId());statement.executeUpdate(); } else try(PreparedStatement statement=connection.prepareStatement("UPDATE portfolio SET quantity=? WHERE id=?")){ statement.setInt(1,next);statement.setInt(2,existing.getId());statement.executeUpdate(); } } }
    private Portfolio map(ResultSet result)throws SQLException{return new Portfolio(result.getInt("id"),result.getInt("user_id"),result.getInt("stock_id"),result.getInt("quantity"),result.getString("symbol"),result.getString("company_name"),result.getDouble("price"));}
}
