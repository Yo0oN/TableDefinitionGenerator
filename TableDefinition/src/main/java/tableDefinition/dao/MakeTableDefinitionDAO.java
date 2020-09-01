package tableDefinition.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MakeTableDefinitionDAO {

	private final String RESOURCE = "myBatisConfig.xml";
	private SqlSession sqlSession;

	private final String QUERY_FIX = "makeTableDefinition"; // final

	public MakeTableDefinitionDAO() {
		InputStream is;
		try {
			is = Resources.getResourceAsStream(RESOURCE);
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
			sqlSession = sqlSessionFactory.openSession();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 테이블 목록 가져오기
	 * @return List<TableDefinition>
	 */
	public List<TableDefinition> getTableList() {
		return sqlSession.selectList(QUERY_FIX + ".getTableList");
	}

	/**
	 * 테이블 상세정보 가져오기
	 * @param tableDefinitionDTO
	 * @return List<TableDefinition>
	 */
	public List<TableDefinition> getTableInfo(TableDefinition tableName) {
		return sqlSession.selectList(QUERY_FIX + ".getTableInfo", tableName);
	}

}
