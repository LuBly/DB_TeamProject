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
			System.out.println(cnt);
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

		String[] area = {"서울","부산","대구","인천","대전","광주","울산","강원","경기","충청남도","충청북도","전라북도","전라남도","경상북도","경상남도","제주도"};
	    String[] accrRqisCn = {"고등학생","고졸","대학생","대학생(휴학)","대학 졸업"};
	    String[] majrRqisCn = {"기계공학과","산업공학과","화학공학과","신소재공학과","응용화학생명공학과","환경안전공학과","건설시스템공학과","교통시스템공학과","건축학과","융합시스템공학과"
	    		,"전자공학과","소프트웨어학과","사이버보안학과","미디어학과","국방디지털융합학과","인공지능융합학과"
	    		,"수학과","물리학과","화학과","생명과학과","경영학과","e-비지니스학과","금융공학과","글로벌경영학과"
	    		,"국어국문학과","사학과","영어영문학과","문화콘텐츠학과","불어불문학과","경제학과","사회학과","행정학과","심리학과","정치외교학과","스포츠레저학과"
	    		,"의학과","간호학과","약학과"};

	    String[] empmSttsCn = new String[200];
        int empm_count = 0;
        
        String[] splzRlmRqisCn = new String[200];
        int splz_count = 0;
        
		try {
			Scanner scan = new Scanner(System.in);
			
			//DB연결
			Connection connection = DriverManager.getConnection(jdbcURL, username, password);
			
			//DB
			Statement curState = connection.createStatement();
			
			//DB에 저장된 테이블이 있다면 지우고 없다면 넘어가기
			String dropTable = "DROP TABLE IF EXISTS UserInfo CASCADE; DROP TABLE IF EXISTS PolicyInfo CASCADE; DROP TABLE IF EXISTS CountInfo CASCADE;";
			//nowState.executeUpdate(DropTable);
			
			//DB 생성
			
			//empmSttsCn : 취업상태 , accrRqisCn : 학력 , majrRqisCn : 전공 , splzRlmRqisCn : 특화분야
			//bizId : 정책 번호 ,polyBizSjnm : 정책명, polyItcnCn : 정책소개, plcyTpNm : 정책유형, rqutPrdCn : 신청기간 , rqutUrla : 주소, polyBizTy : 기관 및 지자채 구분, AgeInfo : 연령, empmSttsCn : 취업상태, accrRqisCn : 학력,  majrRqisCn : 전공, splzRlmRqisCn : 특화분야
			//bizId : 정책 번호, cnt : 검색횟수
			
			String CreateUserInfo = "Create Table UserInfo(userID int,userPassword int, area varchar(20), ageInfo int, empmSttsCn varchar(20),accrRqisCn varchar(20), majrRqisCn varchar(20), splzRlmRqisCn varchar(100));";
            String CreatePolicyInfo = "Create Table PolicyInfo(bizId varchar(100),polyBizTy varchar(100),polyBizSjnm varchar(100),polyItcnCn varchar(200),plcyTpNm varchar(100),ageInfo varchar(100), minAge int, maxAge int, empmSttsCn varchar(100),accrRqisCn varchar(100),majrRqisCn varchar(100), splzRlmRqisCn varchar(100), rqutPrdCn varchar(300), rqutUrla varchar(200));";
            String CreateCountInfo = "Create Table CountInfo(bizId varchar(20),cnt int);";


			curState.executeUpdate(dropTable);
            curState.execute(CreateUserInfo);
            curState.execute(CreatePolicyInfo);
            curState.execute(CreateCountInfo);

			String[][] Data = SplitText.main();


			for(int i = 1; i < 201; i++) {
                System.out.println("\n정책 " + i + "\n");
                String insertSQL_PolicyInfo = "insert into PolicyInfo values(";
                String insertSQL_CountInfo = "insert into CountInfo values(";
                for(int j = 0; j < 6; j++) {
                    insertSQL_PolicyInfo += "'"+ Data[i][j]+"',";
                }

                if(Data[i][5].compareTo("제한없음")==0){//제한없음일 때 min=0, max=99
                    insertSQL_PolicyInfo += "'0','99',";
                }
                else{
                    String Age_num = Data[i][5].replaceAll("[^\\d]","");//나이 영역에서 숫자만 추출 경우1. 만 19세 ~ 34세, 경우2 만18세 이상
                    System.out.println(Age_num);
                    if(Age_num.length()>3){//경우1 1934
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

                insertSQL_PolicyInfo = insertSQL_PolicyInfo.replaceFirst(".$","");//마지막 , 제거
                insertSQL_PolicyInfo += ");";
                insertSQL_CountInfo += "'"+Data[i][0]+"',0);";

                System.out.println(insertSQL_PolicyInfo);
                System.out.println(insertSQL_CountInfo);

                curState.execute(insertSQL_PolicyInfo);
                curState.execute(insertSQL_CountInfo);
                if(Data[i][6].compareTo("제한없음") != 0){
                    empmSttsCn[empm_count] = Data[i][8];
                    empm_count ++;
                }

                if(Data[i][9].compareTo("제한없음") != 0){
                    splzRlmRqisCn[splz_count]=Data[i][9];
                    splz_count ++;
                }
                
                System.out.println("insert "+i);
                System.out.println("\n------------------------------------------");
            }

            System.out.println(empm_count);
            
			for(int i = 1; i < 201; i++) {
				Random random = new Random();
	            System.out.println("\n유저 " + i + "\n");
	            
	            int empm_cur=random.nextInt(empm_count);
	            
	            if(empmSttsCn[empm_cur].length() > 20){
	                empmSttsCn[empm_cur] = "자영업자";
	            }
	            
	            String insertSQL_UserInfo = "insert into UserInfo values('"+i+"','"+i+"','"
	            +area[random.nextInt(area.length)]//지역
	            +"','"+(random.nextInt(60)+20)+"','"//나이 10~70
	            +empmSttsCn[empm_cur]+"','"//취업상태
	            +accrRqisCn[random.nextInt(accrRqisCn.length)]+"','"//학력
	            +majrRqisCn[random.nextInt(majrRqisCn.length)]+"','"//전공
	            +splzRlmRqisCn[random.nextInt(splz_count)]+"');";//특화분야
	            
	            System.out.println(insertSQL_UserInfo);
	            curState.execute(insertSQL_UserInfo);
	            System.out.println("insert "+i);
	            System.out.println("\n------------------------------------------");
	        }
    	    
			
			String userId;
			String userPassword;
			System.out.println("로그인");
			System.out.print("ID: ");
			userId = scan.nextLine();
			System.out.print("Password: ");
			userPassword = scan.nextLine();
			User user = login(userId, userPassword, curState);
			
			System.out.println("Welcome " + user.userId);
			System.out.println("-------------------------------------------");
			int choice;
			String query;
			while(true) {
				System.out.println("1. 나에게 맞는 정책 찾기");
				System.out.println("2. 정책 검색하기");
				System.out.println("3. 실시간 인기 정책순위");
				System.out.println("4. 프로그램 종료");
				choice = scan.nextInt();
				ResultSet result;
				if(choice == 1) {
					System.out.println("나에게 맞는 정책을 찾습니다");
					query = "select distinct policyInfo.* from policyInfo, userInfo where " 
					+ "(" + user.ageInfo + " < policyInfo.maxAge) "
					+ "and (" + user.ageInfo + " > policyInfo.minAge) "
					+ "and ( \'" + user.empmSttsCn + "\' = policyInfo.empmSttsCn or policyInfo.empmSttsCn = \'제한없음\') "
					+ "and ( \'" + user.accrRqisCn + "\' = policyInfo.accrRqisCn or policyInfo.accrRqisCn = \'제한없음\') "
					+ "and ( \'" + user.majrRqisCn + "\' = policyInfo.majrRqisCn or policyInfo.majrRqisCn = \'제한없음\') "
					+ "and ( \'" + user.splzRlmRqisCn + "\' = policyInfo.splzRlmRqisCn or policyInfo.splzRlmRqisCn = \'제한없음\');" ;

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
						System.out.println("검색 결과가 존재하지 않습니다!");
						continue;
					}
					System.out.println("search result");
					System.out.println("-------------------------------");
					while(true) {
						int cnt = 1;
						for(Policy idx : list) {
							System.out.printf("|%3d|%s\n", cnt, idx.polyBizSjnm);
							cnt++;
						}
						System.out.print("자세히 볼 정책번호를 고르시오(이전 화면 -> 0번):");
						int policyChoice = scan.nextInt();
						if(policyChoice == 0)
							break;
						int curIdx = 1;
						for(Policy idx : list) {
							if(curIdx == policyChoice) {
								System.out.println("\n기관 및 지자체: " + idx.polyBizTy);
								System.out.println("정책명: " + idx.polyBizSjnm);
								System.out.println("정책소개: " + idx.polyItcnCn);
								System.out.println("정책유형: " + idx.plcyTpNm);
								if(idx.minAge != 0)
									System.out.println("참여요건 - 연령: " + idx.minAge + "~" + idx.maxAge);
								else
									System.out.println("참여요건 - 연령: 제한없음");
								System.out.println("참여요건 - 취업상태: " + idx.empmSttsCn);
								System.out.println("참여요건 - 학력: " + idx.accrRqisCn);
								System.out.println("참여요건 - 전공: " + idx.majrRqisCn);
								System.out.println("참여요건 - 특화분야: " + idx.splzRlmRqisCn);
								System.out.println("신청기간: " + idx.rqutPrdCn);
								System.out.println("사이트 링크: " + idx.rqutUrla);
								break;
							}
							curIdx++;
						}
						int back = 0;
						while (back != 1) {
							System.out.print("이전 화면으로 돌아가려면 1을 입력해주세요: ");
							back = scan.nextInt();
							System.out.println();
						}
					}
				}
				else if(choice == 2) {
					System.out.print("검색어를 입력해 주세요:");
					String keyword = scan.next();
					query = "select distinct * from policyInfo where polyBizSjnm like \'" + "%" + keyword + "%\';";
					System.out.println(query);
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
						System.out.println("There is no result");
						continue;
					}
					System.out.println("search result");
					System.out.println("-------------------------------");
					while(true) {
						int cnt = 1;
						for(Policy idx : list) {
							System.out.printf("|%3d|%s|\n", cnt, idx.polyBizSjnm);
							cnt++;
						}
						System.out.print("자세히 볼 정책번호를 고르시오(이전 화면 -> 0번):");
						int policyChoice = scan.nextInt();
						if(policyChoice == 0)
							break;
						int curIdx = 1;
						for(Policy idx : list) {
							if(curIdx == policyChoice) {
								System.out.println("기관 및 지자체: " + idx.polyBizTy);
								System.out.println("정책명: " + idx.polyBizSjnm);
								System.out.println("정책소개: " + idx.polyItcnCn);
								System.out.println("정책유형: " + idx.plcyTpNm);
								if(idx.minAge != 0)
									System.out.println("참여요건 - 연령: " + idx.minAge + "~" + idx.maxAge);
								else
									System.out.println("참여요건 - 연령: 제한없음");
								System.out.println("참여요건 - 취업상태: " + idx.empmSttsCn);
								System.out.println("참여요건 - 학력: " + idx.accrRqisCn);
								System.out.println("참여요건 - 전공: " + idx.majrRqisCn);
								System.out.println("참여요건 - 특화분야: " + idx.splzRlmRqisCn);
								System.out.println("신청기간: " + idx.rqutPrdCn);
								System.out.println("사이트 링크: " + idx.rqutUrla);
								break;
							}
							curIdx++;
						}
					}
					query = "update countInfo set cnt = cnt + 1 where countinfo.bizId in (select bizId from policyInfo where polyBizSjnm like " + "\'%" + keyword + "%\');";
					curState.execute(query);
				}
				else if(choice == 3) {
					query = "Select polyBizSjnm From poliyInfo Where poliyInfo.bizID = \r\n"
					+ "(select countInfo.bizID From countInfo  order by cnt desc limit 10);";
					result = curState.executeQuery(query);
					int cnt = 1;
					System.out.println("실시간 정책 순위");
					System.out.println("------------------------------");
					while(result.next()) {
						String polyBizSjnm = result.getString("polyBizSjnm");
						System.out.println(cnt + ". " + polyBizSjnm);
						cnt++;
					}
				}
				else if(choice == 4) {
					System.out.println("Program is finished");
					connection.close();
					System.exit(0);
				}
				else {
					System.out.println("number must be 1 ~ 4");
				}
			}
		} catch(SQLException e) {
			System.out.println(e);
		}
	}
}