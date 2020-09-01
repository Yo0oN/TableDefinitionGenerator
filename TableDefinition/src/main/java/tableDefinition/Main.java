package tableDefinition;

import tableDefinition.service.TableDefinitionService;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TableDefinitionService td = new TableDefinitionService();
		boolean result = td.tableDefinitionGenerator();
		
		if (result == true) {
			System.out.println("파일 생성 성공!");
		} else {
			System.out.println("파일 생성 실패!");
		}
	}

}
