import java.sql.*;
import java.util.*;

public class ConnectDB {
	
	public static User login(String userID, String userPassword, Statement curState) {
		try {
			String query = "select * from Userinfo where userId = " + userID + " and userPassword = " + userPassword + ";";
			ResultSet result = curState.executeQuery(query);
			User user = new User();
			int cnt = 0;
			while(result.next()) {
				user.userId = result.getString("userId");
				user.userPassword = result.getString("userPassword");
				user.area = result.getString("area");
				user.ageInfo = result.getInt("ageInfo");
				user.empmSttsCn = result.getString("empmSttsCn");
				user.accrRqisCn = result.getString("accrRqisCn");
				user.majrRqisCn = result.getString("majrRqisCn");
				user.splzRlmRqisCn = result.getString("splzRlmRqisCn");
				cnt++;
			}
			if(cnt == 1)
				return user;
		}
		catch (SQLException e){
			System.out.println("Error in Login");
			e.printStackTrace();
		}
		System.err.println("Error in Login");
		return null;
	}
	
	public static void main(String[] args) {
		
		String jdbcURL = "jdbc:postgresql://localhost/Teamproject";
		String username = "postgres";
		String password = "1234";

		String[] area = {"����","�λ�","�뱸","��õ","����","����","���","����","���","��û����","��û�ϵ�","����ϵ�","���󳲵�","���ϵ�","��󳲵�","���ֵ�"};
	    String[] accrRqisCn = {"����л�","����","���л�","���л�(����)","���� ����"};
	    String[] majrRqisCn = {"�����а�","������а�","ȭ�а��а�","�ż�����а�","����ȭ�л�����а�","ȯ��������а�","�Ǽ��ý��۰��а�","����ý��۰��а�","�����а�","���սý��۰��а�"
	    		,"���ڰ��а�","����Ʈ�����а�","���̹������а�","�̵���а�","��������������а�","�ΰ����������а�"
	    		,"���а�","�����а�","ȭ�а�","������а�","�濵�а�","e-�����Ͻ��а�","�������а�","�۷ι��濵�а�"
	    		,"������а�","���а�","������а�","��ȭ�������а�","�Ҿ�ҹ��а�","�����а�","��ȸ�а�","�����а�","�ɸ��а�","��ġ�ܱ��а�","�����������а�"
	    		,"���а�","��ȣ�а�","���а�"};

	    String[] empmSttsCn = new String[200];
        int empm_count = 0;
        
        String[] splzRlmRqisCn = new String[200];
        int splz_count = 0;
        
		try {
			Scanner scan = new Scanner(System.in);
			
			//DB����
			Connection connection = DriverManager.getConnection(jdbcURL, username, password);
			
			//DB
			Statement curState = connection.createStatement();
			
			//DB�� ����� ���̺��� �ִٸ� ����� ���ٸ� �Ѿ��
			String dropTable = "DROP TABLE IF EXISTS UserInfo CASCADE; DROP TABLE IF EXISTS PolicyInfo CASCADE; DROP TABLE IF EXISTS CountInfo CASCADE;";
			//nowState.executeUpdate(DropTable);
			
			//DB ����
			
			//empmSttsCn : ������� , accrRqisCn : �з� , majrRqisCn : ���� , splzRlmRqisCn : Ưȭ�о�
			//bizId : ��å ��ȣ ,polyBizSjnm : ��å��, polyItcnCn : ��å�Ұ�, plcyTpNm : ��å����, rqutPrdCn : ��û�Ⱓ , rqutUrla : �ּ�, polyBizTy : ��� �� ����ä ����, AgeInfo : ����, empmSttsCn : �������, accrRqisCn : �з�,  majrRqisCn : ����, splzRlmRqisCn : Ưȭ�о�
			//bizId : ��å ��ȣ, cnt : �˻�Ƚ��
			
			String CreateUserInfo = "Create Table UserInfo(userID int,userPassword int, area varchar(20), ageInfo int, empmSttsCn varchar(20),accrRqisCn varchar(20), majrRqisCn varchar(20), splzRlmRqisCn varchar(100));";
            String CreatePolicyInfo = "Create Table PolicyInfo(bizId varchar(20),polyBizTy varchar(100),polyBizSjnm varchar(100),polyItcnCn varchar(200),plcyTpNm varchar(100),ageInfo varchar(100), minAge int, maxAge int, empmSttsCn varchar(100),accrRqisCn varchar(100),majrRqisCn varchar(100), splzRlmRqisCn varchar(100), rqutPrdCn varchar(300), rqutUrla varchar(200));";
            String CreateCountInfo = "Create Table CountInfo(bizId varchar(20),cnt int);";
            String CreateCountInfoTrigger = "CREATE OR REPLACE FUNCTION trigger_insert_Count()\r\n"
            		+ "returns trigger\r\n"
            		+ "AS $$\r\n"
            		+ "DECLARE\r\n"
            		+ "BEGIN\r\n"
            		+ "   Insert into CountInfo values (New.bizID,0);\r\n"
            		+ "   return NULL;\r\n"
            		+ "END; $$\r\n"
            		+ "LANGUAGE 'plpgsql';\r\n"
            		+ "\r\n"
            		+ "create trigger Insert_Count\r\n"
            		+ "after insert on PolicyInfo\r\n"
            		+ "for each row\r\n"
            		+ "execute procedure trigger_insert_Count()";
            
			curState.executeUpdate(dropTable);
            curState.execute(CreateUserInfo);
            curState.execute(CreatePolicyInfo);
            curState.execute(CreateCountInfo);
            curState.execute(CreateCountInfoTrigger);

			String[][] Data = SplitText.main();


			for(int i = 1; i < 198; i++) {
                //System.out.println("\n��å " + i + "\n");
                String insertSQL_PolicyInfo = "insert into PolicyInfo values(";
//                String insertSQL_CountInfo = "insert into CountInfo values(";
                for(int j = 0; j < 6; j++) {
                    insertSQL_PolicyInfo += "'"+ Data[i][j]+"',";
                }

                if(Data[i][5].compareTo("���Ѿ���")==0){//���Ѿ����� �� min=0, max=99
                    insertSQL_PolicyInfo += "'0','99',";
                }
                else{
                    String Age_num = Data[i][5].replaceAll("[^\\d]","");//���� �������� ���ڸ� ���� ���1. �� 19�� ~ 34��, ���2 ��18�� �̻�
                    //System.out.println(Age_num);
                    if(Age_num.length()>3){//���1 1934
                        String minAge = Age_num.substring(0,2);
                        String maxAge = Age_num.substring(2);
                        insertSQL_PolicyInfo += "'"+minAge+"','"+maxAge+"',";
                    }
                    else if(Age_num.length()==3){
                        String minAge = Age_num.substring(0,1);
                        String maxAge = Age_num.substring(1);
                        insertSQL_PolicyInfo += "'"+minAge+"','"+maxAge+"',";
                    }
                    else{
                        insertSQL_PolicyInfo += "'"+Age_num+"','99',";
                    }
                }
                
                for(int j = 8; j < 14; j++) {
                    insertSQL_PolicyInfo += "'"+ Data[i][j-2]+"',";
                }

                insertSQL_PolicyInfo = insertSQL_PolicyInfo.replaceFirst(".$","");//������ , ����
                insertSQL_PolicyInfo += ");";
//                insertSQL_CountInfo += "'"+Data[i][0]+"',0);";

                //System.out.println(insertSQL_PolicyInfo);
//                System.out.println(insertSQL_CountInfo);

                curState.execute(insertSQL_PolicyInfo);
//                curState.execute(insertSQL_CountInfo);
                if(Data[i][6].compareTo("���Ѿ���") != 0){
                    empmSttsCn[empm_count] = Data[i][6];
                    empm_count ++;
                }

                if(Data[i][9].compareTo("���Ѿ���") != 0){
                    splzRlmRqisCn[splz_count]=Data[i][9];
                    splz_count ++;
                }
                
                //System.out.println("insert "+i);
                //System.out.println("\n------------------------------------------");
            }
			splzRlmRqisCn[splz_count++] = "-";
			
			for(int i = 1; i < 198; i++) {
				Random random = new Random();
	            //System.out.println("\n���� " + i + "\n");
	            
	            int empm_cur=random.nextInt(empm_count);
	            
	            if(empmSttsCn[empm_cur].length() > 20){
	                empmSttsCn[empm_cur] = "�ڿ�����";
	            }
	            String accr = accrRqisCn[random.nextInt(accrRqisCn.length)];
	            String majr = majrRqisCn[random.nextInt(majrRqisCn.length)];
	            if(accr.compareTo("����") == 0 || accr.compareTo("����л�") == 0) {
	            	majr = "-";
	            }
	            String insertSQL_UserInfo = "insert into UserInfo values('"+i+"','"+i+"','"
	            +area[random.nextInt(area.length)]//����
	            +"','"+(random.nextInt(60)+20)+"','"//���� 10~70
	            +empmSttsCn[empm_cur]+"','"//�������
	            +accr +"','"//�з�
	            +majr +"','"//����
	            +splzRlmRqisCn[random.nextInt(splz_count)]+"');";//Ưȭ�о�
	            
	            //System.out.println(insertSQL_UserInfo);
	            curState.execute(insertSQL_UserInfo);
	            //System.out.println("insert "+i);
	            //System.out.println("\n------------------------------------------");
	        }
    	    
			
			String userId;
			String userPassword;
			System.out.println("�α���");
			System.out.print("ID: ");
			userId = scan.nextLine();
			System.out.print("Password: ");
			userPassword = scan.nextLine();
			User user = login(userId, userPassword, curState);
			while(user == null) {
				System.out.println("�α���");
				System.out.print("ID: ");
				userId = scan.nextLine();
				System.out.print("Password: ");
				userPassword = scan.nextLine();
				user = login(userId, userPassword, curState);
			}
			System.out.println("\nWelcome " + user.userId);
			int choice;
			String query;
			while(true) {
				System.out.println("-------------------------------------------");
				System.out.println("1. ������ �´� ��å ã��");
				System.out.println("2. ��å �˻��ϱ�");
				System.out.println("3. �ǽð� �α� ��å����");
				System.out.println("4. ���α׷� ����");
				System.out.print("��ȣ�� �Է��ϼ���(1 ~ 4) : ");
				choice = scan.nextInt();
				ResultSet result;
				if(choice == 1) {
					System.out.println("\n������ �´� ��å�� ã���ϴ�");
					query = "select distinct policyInfo.* from policyInfo, userInfo where " 
					+ "(" + user.ageInfo + " < policyInfo.maxAge) "
					+ "and (" + user.ageInfo + " > policyInfo.minAge) "
					+ "and ( \'" + user.empmSttsCn + "\' = policyInfo.empmSttsCn or policyInfo.empmSttsCn = \'���Ѿ���\') "
					+ "and ( \'" + user.accrRqisCn + "\' = policyInfo.accrRqisCn or policyInfo.accrRqisCn = \'���Ѿ���\') "
					+ "and ( \'" + user.majrRqisCn + "\' = policyInfo.majrRqisCn or policyInfo.majrRqisCn = \'���Ѿ���\') "
					+ "and ( \'" + user.splzRlmRqisCn + "\' = policyInfo.splzRlmRqisCn or policyInfo.splzRlmRqisCn = \'���Ѿ���\');" ;

					result = curState.executeQuery(query);
					ArrayList<Policy> list = new ArrayList<> ();
					int check = 0;
					while(result.next()) {
						Policy policy = new Policy();
						policy.bizId = result.getString("bizId");
						policy.polyBizSjnm = result.getString("polyBizSjnm");
						policy.polyItcnCn = result.getString("polyItcnCn");
						policy.plcyTpNm = result.getString("plcyTpNm");
						policy.rqutPrdCn = result.getString("rqutPrdCn");
						policy.rqutUrla = result.getString("rqutUrla");
						policy.polyBizTy = result.getString("polyBizTy");
						policy.minAge = result.getInt("minAge");
						policy.maxAge = result.getInt("maxAge");
						policy.empmSttsCn = result.getString("empmSttsCn");
						policy.accrRqisCn = result.getString("accrRqisCn");
						policy.majrRqisCn = result.getString("majrRqisCn");
						policy.splzRlmRqisCn = result.getString("splzRlmRqisCn");
						list.add(policy);
						check = 1;
					}
					if(check == 0) {
						System.out.println("\n�˻� ����� �������� �ʽ��ϴ�!");
						System.out.println("���� ȭ������ ���ư��ϴ�\n");
						continue;
					}
					while(true) {
						System.out.println("�˻����");
						System.out.println("-------------------------------");
						int cnt = 1;
						for(Policy idx : list) {
							System.out.printf("|%3d|%s\n", cnt, idx.polyBizSjnm);
							cnt++;
						}
						System.out.print("�ڼ��� �� ��å��ȣ�� ���ÿ�(���� ȭ�� -> 0��):");
						int policyChoice = scan.nextInt();
						if(policyChoice > cnt - 1 || policyChoice < 0) {
							System.out.printf("\n��å��ȣ�� ������ %d���� %d���� �����մϴ�!", 1, cnt - 1);
							int back = -1;
							while (back != 0) {
								System.out.print("\n���� ȭ������ ���ư����� 0�� �Է����ּ���: ");
								back = scan.nextInt();
								System.out.println();
							}
							continue;
						}
						if(policyChoice == 0)
							break;
						int curIdx = 1;
						for(Policy idx : list) {
							if(curIdx == policyChoice) {
								System.out.println("\n-------------------------------");
								System.out.println("��� �� ����ü: " + idx.polyBizTy);
								System.out.println("��å��: " + idx.polyBizSjnm);
								System.out.println("��å�Ұ�: " + idx.polyItcnCn);
								System.out.println("��å����: " + idx.plcyTpNm);
								if(idx.minAge != 0)
									System.out.println("������� - ����: " + idx.minAge + "~" + idx.maxAge);
								else
									System.out.println("������� - ����: ���Ѿ���");
								System.out.println("������� - �������: " + idx.empmSttsCn);
								System.out.println("������� - �з�: " + idx.accrRqisCn);
								System.out.println("������� - ����: " + idx.majrRqisCn);
								System.out.println("������� - Ưȭ�о�: " + idx.splzRlmRqisCn);
								System.out.println("��û�Ⱓ: " + idx.rqutPrdCn);
								System.out.println("����Ʈ ��ũ: " + idx.rqutUrla);
								break;
							}
							curIdx++;
						}
						int back = -1;
						while (back != 0) {
							System.out.print("\n���� ȭ������ ���ư����� 0�� �Է����ּ���: ");
							back = scan.nextInt();
							System.out.println();
						}
					}
				}
				else if(choice == 2) {
					System.out.println("\nŰ���� �˻��� �����մϴ�");
					System.out.print("�˻�� �Է��� �ּ���:");
					String keyword = scan.next();
					query = "select * from policyInfo where polyBizSjnm like \'" + "%" + keyword + "%\';";
					result = curState.executeQuery(query);
					ArrayList<Policy> list = new ArrayList<> ();
					int check = 0;
					while(result.next()) {
						Policy policy = new Policy();
						policy.bizId = result.getString("bizId");
						policy.polyBizSjnm = result.getString("polyBizSjnm");
						policy.polyItcnCn = result.getString("polyItcnCn");
						policy.plcyTpNm = result.getString("plcyTpNm");
						policy.rqutPrdCn = result.getString("rqutPrdCn");
						policy.rqutUrla = result.getString("rqutUrla");
						policy.polyBizTy = result.getString("polyBizTy");
						policy.minAge = result.getInt("minAge");
						policy.maxAge = result.getInt("maxAge");
						policy.empmSttsCn = result.getString("empmSttsCn");
						policy.accrRqisCn = result.getString("accrRqisCn");
						policy.majrRqisCn = result.getString("majrRqisCn");
						policy.splzRlmRqisCn = result.getString("splzRlmRqisCn");
						list.add(policy);
						check = 1;
					}
					if(check == 0) {
						System.out.println("\n�˻� ����� �������� �ʽ��ϴ�!");
						System.out.println("���� ȭ������ ���ư��ϴ�\n");
						continue;
					}
					while(true) {
						System.out.println("\n�˻����");
						System.out.println("-------------------------------");
						int cnt = 1;
						for(Policy idx : list) {
							System.out.printf("|%3d|%s\n", cnt, idx.polyBizSjnm);
							cnt++;
						}
						System.out.print("\n�ڼ��� �� ��å��ȣ�� ���ÿ�(���� ȭ�� -> 0��):");
						int policyChoice = scan.nextInt();
						if(policyChoice > cnt - 1 || policyChoice < 0) {
							System.out.printf("\n��å��ȣ�� ������ %d���� %d���� �����մϴ�!", 1, cnt - 1);
							int back = -1;
							while (back != 0) {
								System.out.print("\n���� ȭ������ ���ư����� 0�� �Է����ּ���: ");
								back = scan.nextInt();
								System.out.println();
							}
							continue;
						}
						if(policyChoice == 0)
							break;
						int curIdx = 1;
						for(Policy idx : list) {
							if(curIdx == policyChoice) {
								System.out.println("\n-------------------------------");
								System.out.println("��� �� ����ü: " + idx.polyBizTy);
								System.out.println("��å��: " + idx.polyBizSjnm);
								System.out.println("��å�Ұ�: " + idx.polyItcnCn);
								System.out.println("��å����: " + idx.plcyTpNm);
								if(idx.minAge != 0)
									System.out.println("������� - ����: " + idx.minAge + "~" + idx.maxAge);
								else
									System.out.println("������� - ����: ���Ѿ���");
								System.out.println("������� - �������: " + idx.empmSttsCn);
								System.out.println("������� - �з�: " + idx.accrRqisCn);
								System.out.println("������� - ����: " + idx.majrRqisCn);
								System.out.println("������� - Ưȭ�о�: " + idx.splzRlmRqisCn);
								System.out.println("��û�Ⱓ: " + idx.rqutPrdCn);
								System.out.println("����Ʈ ��ũ: " + idx.rqutUrla);
								break;
							}
							curIdx++;
						}
						int back = -1;
						while (back != 0) {
							System.out.print("\n���� ȭ������ ���ư����� 0�� �Է����ּ���: ");
							back = scan.nextInt();
							System.out.println();
						}
					}
					query = "update countInfo set cnt = cnt + 1 where countinfo.bizId in (select bizId from policyInfo where polyBizSjnm like " + "\'%" + keyword + "%\');";
					curState.execute(query);
				}
				else if(choice == 3) {
					query = "Select polyBizSjnm \r\n"
							+ "From policyInfo,countInfo\r\n"
							+ "Where policyInfo.bizID = countInfo.bizID order by cnt desc limit 10;";
					result = curState.executeQuery(query);
					int cnt = 1;
					System.out.println("\n�ǽð� ��å ����");
					System.out.println("------------------------------");
					while(result.next()) {
						String polyBizSjnm = result.getString("polyBizSjnm");
						System.out.println(cnt + ". " + polyBizSjnm);
						cnt++;
					}
				}
				else if(choice == 4) {
					System.out.println("���α׷��� �����մϴ�.");
					connection.close();
					System.exit(0);
				}
				else {
					System.out.println("���ڴ� 1 ~ 4 ���� ���� �־�� �մϴ�!");
				}
			}
		} catch(SQLException e) {
			System.out.println(e);
		}
	}
}