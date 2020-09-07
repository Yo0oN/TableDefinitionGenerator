package tableDefinition.service;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tableDefinition.dao.MakeTableDefinitionDAO;
import tableDefinition.dao.TableDefinition;
import util.ExcelWriter;

public class TableDefinitionService {
	private static final Logger logger = LogManager.getLogger(TableDefinition.class);

	MakeTableDefinitionDAO makeTableDefinitionDAO = new MakeTableDefinitionDAO();
	ExcelWriter excelUtil = new ExcelWriter();
	private long startTime;
	private long endTime;
    
	/**
	 * 테이블 정의서 생성
	 */
	@SuppressWarnings("resource")
	public boolean tableDefinitionGenerator () {
		startTime = new Date().getTime();
		
		List<TableDefinition> tableListResult = getTableList(); // 테이블 목록 읽어오기
		
		// 이것은 guava의 null 확인법이다. 이 외에 apache의 common.lang도 관련 기능을 제공해준다.
		// null 발생 시 Exception 발생시 중단.
		checkArgument(tableListResult != null && tableListResult.size() > 0, "list가 null이거나 사이즈가 0 입니다.");

		excelUtil.makeTableListSheet(tableListResult); // 테이블 목록 시트 작성하기
		
		endTime = new Date().getTime();
		logger.warn("목록 읽고 쓰기 : " + (endTime - startTime) + "밀리초");
		
		tableListResult.stream() // stream : 연속된 데이터를 처리(collection)
					.forEach(tableDefinition -> { // 반복문을 사용한 것처럼 컬렉션의 모든 요소들을 차례로 하나씩 꺼내준다. tableListResult의 결과를 하나씩 빼서 tableDefinition에 담음
						startTime = new Date().getTime(); // 중복되는 부분 빼서 시간 출력용을 위한 메서드 만들어보기

						List<TableDefinition> tableInfoResult = getTableListInfo(tableDefinition); // 테이블 상세 정보 읽어오기
						excelUtil.makeTableInfoTab(tableInfoResult); // 테이블 상세 정보 작성하기

						endTime = new Date().getTime();
						
						logger.warn("{} 상세정보 읽고 쓰기 : {} 밀리초", tableDefinition.getTABLE_NAME(), endTime - startTime);
					});

		return excelUtil.excelGenerator();
	}
	
    /**
     * 테이블 목록 가져오기
     * @return boolean
     */
	public List<TableDefinition> getTableList() {
		List<TableDefinition> tableListResult = makeTableDefinitionDAO.getTableList();
		return tableListResult; 
	}
	
    /**
     * 테이블 상세정보 가져오기
     * @return boolean
     */
	public List<TableDefinition> getTableListInfo(TableDefinition tableName) {
		List<TableDefinition> tableInfoResult = makeTableDefinitionDAO.getTableInfo(tableName);

		return tableInfoResult; 
	}
}
