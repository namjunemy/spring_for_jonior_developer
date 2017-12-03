package com.javalec.spring_project_board_dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.javalec.spring_project_board_dto.BDto;
import com.javalec.spring_project_board_util.Constant;

public class BDao {
  DataSource dataSource;
  JdbcTemplate template = null;

  public BDao() {
    template = Constant.template;
  }

  public void reply(final String bId, final String bName, final String bTitle, final String bContent,
                    final String bGroup, final String bStep, final String bIndent) {
    replyShape(bGroup, bStep);
    String query = "insert into mvc_board (bName, bTitle, bContent, bGroup, bStep, bIndent) " + "values (?,?,?,?,?,?)";
    template.update(query, new PreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, bName);
        ps.setString(2, bTitle);
        ps.setString(3, bContent);
        ps.setInt(4, Integer.parseInt(bGroup));
        ps.setInt(5, Integer.parseInt(bStep) + 1);
        ps.setInt(6, Integer.parseInt(bIndent) + 1);
      }
    });
  }

  private void replyShape(final String strGroup, final String strStep) {
    String query = "update mvc_board set bStep = bStep + 1 where bGroup = ? and bStep > ?";
    template.update(query, new PreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setInt(1, Integer.parseInt(strGroup));
        ps.setInt(2, Integer.parseInt(strStep));
      }
    });
  }

  public BDto replyView(String strId) {
    String query = "select * from mvc_board where bId = " + strId;
    return template.queryForObject(query, new BeanPropertyRowMapper<BDto>(BDto.class));
  }

  public void delete(final String bId) {
    String query = "delete from mvc_board where bId = ?";
    template.update(query, new PreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, bId);
      }
    });
  }

  public void modify(final String bId, final String bName, final String bTitle, final String bContent) {
    String query = "update mvc_board set bName = ?, bTitle = ?, bContent = ? where bId = ?";
    template.update(query, new PreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, bName);
        ps.setString(2, bTitle);
        ps.setString(3, bContent);
        ps.setInt(4, Integer.parseInt(bId));
      }
    });
  }

  public BDto contentView(String strId) {
    upHit(strId);

    String query = "select * from mvc_board where bId = " + strId;
    return template.queryForObject(query, new BeanPropertyRowMapper<BDto>(BDto.class));
  }

  private void upHit(final String bId) {
    String query = "update mvc_board set bHit = bHit + 1 where bId = ?";
    template.update(query, new PreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setInt(1, Integer.parseInt(bId));
      }
    });
  }

  public void write(final String bName, final String bTitle, final String bContent) {
    template.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        String query = "INSERT INTO mvc_board(bName, bTitle, bContent, bHit, bGroup, bStep, bIndent)"
            + "VALUE (?, ?, ?, 0, 0, 0, 0)";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, bName);
        pstmt.setString(2, bTitle);
        pstmt.setString(3, bContent);
        return pstmt;
      }
    });
  }

  public ArrayList<BDto> list() {
    String query = "select bId, bName, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent"
        + " from mvc_board order by bGroup desc, bStep asc";
    return (ArrayList<BDto>) template.query(query, new BeanPropertyRowMapper<BDto>(BDto.class));
  }
}
