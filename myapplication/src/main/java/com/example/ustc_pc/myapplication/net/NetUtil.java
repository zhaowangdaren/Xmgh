package com.example.ustc_pc.myapplication.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.unit.AssessmentScore;
import com.example.ustc_pc.myapplication.unit.FileOperation;
import com.example.ustc_pc.myapplication.unit.Paper;
import com.example.ustc_pc.myapplication.unit.Question;
import com.example.ustc_pc.myapplication.unit.Strings;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class NetUtil {

    /**
     *
     * @return txt content(json)
     */
    public static String checkUpdate(){
        String result = "";
        try {
            URL url = new URL(Strings.URL_CHECK_UPDATE);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.connect();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return "Server returned HTTP "+ connection.getResponseCode()+" "+
                        connection.getResponseMessage();
            }
            InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStream);
            String temp = bufferedReader.readLine();
            while( temp != null ){
                result += temp;
                temp = bufferedReader.readLine();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static boolean saveResponseAsFile(String url, String filePath){
        InputStream inputStream  = null;
        OutputStream outputStream = null;
        HttpURLConnection connection = null;
        try{
            URL urlR = new URL(url);
            connection = (HttpURLConnection)urlR.openConnection();
            connection.setConnectTimeout( 5 * 1000);
            connection.setRequestMethod("GET");
            if( connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                inputStream = connection.getInputStream();
                outputStream = new FileOutputStream(filePath);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while((count = inputStream.read(data)) != -1){
                    total += count;
                    outputStream.write(data, 0 ,count);
                }
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(outputStream != null) outputStream.close();
                if(inputStream != null) inputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(connection != null)connection.disconnect();
        }
        return false;
    }


	public static Bitmap getImageFromServer(String path) {
		// TODO Auto-generated method stub
        if(path != null && path.length() > 0)
		try{
			URL url = new URL(path);  
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	        conn.setConnectTimeout(5 * 1000);  
	        conn.setRequestMethod("GET");
	        InputStream resultIS ;
	        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){  
	            resultIS = conn.getInputStream();  
	        }else{
	        	return null;
	        }   
			Bitmap bitmap = BitmapFactory.decodeStream(resultIS);
			return bitmap;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 发布任务
	 * @param content
	 * @param location
	 * @param number
	 * @param contactNumber
	 * @return
	 */
	public Boolean publishTask(String content, String location, String number, String contactNumber){
		
		return false;
	}
	
	 


    public static AssessmentScore getAssessmentScore2(String accountNumber){
        InputStream inputStream = null;
        OutputStream outputStream = null;
        HttpURLConnection connection = null;
        try{
            URL url = new URL(Strings.ASSESSMENT_URL);

            connection = (HttpURLConnection)url.openConnection();
            connection.connect();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
//                return "Server returned HTTP "+ connection.getResponseCode()+" "+
//                        connection.getResponseMessage();
                return null;
            }
            //This will be useful to display download percentage
            //might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            //download the file
            inputStream = connection.getInputStream();

            outputStream = new FileOutputStream(FileOperation.APP_PATH + "assessment_info.json");
            byte data[] = new byte[4096];
            int count;
            while((count = inputStream.read(data)) != -1){
                outputStream.write(data, 0, count);
            }
            String resultJSON = FileOperation.getFileFromSD(FileOperation.APP_PATH + "assessment_info.json");
            return FileOperation.getAssessmentScoreFromJSONStr(resultJSON);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(outputStream != null)
                    outputStream.close();
                if(inputStream != null)
                    inputStream.close();
            }catch (IOException ignored){}
            if(connection != null)
                connection.disconnect();
        }
        return null;
    }
	/**
	 * -1 unknow error
	 * 0 error account
	 * 1 password error
     * if login success then return personal.jaon
	 * @param accountNumber
	 * @param password
	 * @return
	 */
	public static String login(String accountNumber, String password) {
		// TODO Auto-generated method stub
//        String encriptPassword = SHA1(password);
//        if(encriptPassword != null)password = encriptPassword;

		HttpPost httpRequest = new HttpPost(Strings.LOGIN_URL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("account_name",accountNumber));
		params.add(new BasicNameValuePair("password",password));
		try{
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = httpResponse.getEntity();
				if( entity != null){
					String resultJSON = readInputStream(entity.getContent());
					return resultJSON;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			
		}
		return null;

	}
	 
	private static String SHA1(String decript){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            //Create Hex String
            StringBuffer hexString = new StringBuffer();
            for(int i =0; i< messageDigest.length; i++){
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if(shaHex.length() < 2){
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }

	private static String readInputStream(InputStream content) throws IOException {
		// TODO Auto-generated method stub
		String result = "";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
        int length = 0;  
        byte[] data = new byte[1024];  
        try {  
            while (-1 != (length = content.read(data))) {  
                outputStream.write(data, 0, length);  
            }  
            result = new String(outputStream.toByteArray());
            int resultLength = result.length();
        } catch (Exception e) {  
            // TODO: handle exception
            e.printStackTrace();
        }  
        return result;
		
	}

    public static String getCoursesFromServer(){
        HttpPost httpRequest = new HttpPost(Strings.COURSES_URL);
        //List<NameValuePair> params = new ArrayList<NameValuePair>();
        try{
            //httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if( entity != null){
                    String result = readInputStream(entity.getContent());
                    return result;
                }
            }
        }catch(Exception e){
            e.printStackTrace();

        }
        return null;
    }

    public static String getKPPapers(String kpID) {
        HttpPost httpRequest = new HttpPost(Strings.KPPAPERS_URL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("kp_id",kpID));
        try{
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if( entity != null){
                    String result = readInputStream(entity.getContent());
                    return result;
                }
            }
        }catch(Exception e){
            e.printStackTrace();

        }
        return null;
    }

    public static Paper getPaper(String id) {
        HttpPost httpRequest = new HttpPost(Strings.PAPER_URL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("paper_id",id));
        try{
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if( entity != null){
                    String result = readInputStream(entity.getContent());
                    if(result != null && result.length() > 0)return FileOperation.getPaperFromJSONStr(result);

                }
            }
        }catch(Exception e){
            e.printStackTrace();

        }
        return null;
    }

    public static int regist(String username, String password){
        HttpPost httpRequest = new HttpPost(Strings.REGISTER_URL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("telephone",username));
        params.add(new BasicNameValuePair("password",password));
        params.add(new BasicNameValuePair("type", "1"));
        try{
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if( entity != null){
                    String result = readInputStream(entity.getContent());
                    int iResult = Integer.valueOf(result);
                    return iResult;
                }
            }
        }catch(Exception e){
            e.printStackTrace();

        }
        return 2;
    }

    public static int uploadingUserInfo(Context context) {
        UserSharedPreference userSharedPreference = new UserSharedPreference(context);
        HttpPost httpPost = new HttpPost(Strings.REGISTER_URL);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("telephone",userSharedPreference.getAccountNumber()));
        params.add(new BasicNameValuePair("password",userSharedPreference.getPassword()));
        params.add(new BasicNameValuePair("type","2"));
        params.add(new BasicNameValuePair("nickName",userSharedPreference.getStrUserName()));
        params.add(new BasicNameValuePair("realName", userSharedPreference.getRealName()));
        params.add(new BasicNameValuePair("gender",String.valueOf(userSharedPreference.getGenderI())));
        params.add(new BasicNameValuePair("userType",String.valueOf(userSharedPreference.getUserType())));
        params.add(new BasicNameValuePair("email", userSharedPreference.getEmail()));
        params.add(new BasicNameValuePair("aboutMe",userSharedPreference.getAboutMe()));
        params.add(new BasicNameValuePair("sourceCollege",userSharedPreference.getSourceCollege()));
        params.add(new BasicNameValuePair("sourceMajor", userSharedPreference.getSourceMajor()));
        params.add(new BasicNameValuePair("firstTargetCollege",userSharedPreference.getFirstTC()));
        params.add(new BasicNameValuePair("firstTargetMajor",userSharedPreference.getFirstTM()));
        params.add(new BasicNameValuePair("secondTargetCollege",userSharedPreference.getSecondTC()));
        params.add(new BasicNameValuePair("secondTargetMajor",userSharedPreference.getSecondTM()));
        params.add(new BasicNameValuePair("acceptedCollege", userSharedPreference.getAcceptedC()));
        params.add(new BasicNameValuePair("acceptedMajor",userSharedPreference.getAcceptedM()));
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
            int status = httpResponse.getStatusLine().getStatusCode();
            if( status== HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    String result = readInputStream(entity.getContent());
                    int iResult = Integer.valueOf(result);
                    if(iResult == 1)userSharedPreference.setIsUserInfoChanged(false);
                    return iResult;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
	/**
	 * 
	 * @param username
	 * @param password
	 * @param email
	 * @param sex
	 * @return 0-success;1-username exist;2-register failed
	 */
	public static int register(String username, String password, String email,
			int sex) {
		// TODO Auto-generated method stub
		HttpPost httpRequest = new HttpPost(Strings.REGISTER_URL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username",username));
		params.add(new BasicNameValuePair("password",password));
		params.add(new BasicNameValuePair("email",email));
		params.add(new BasicNameValuePair("sex",String.valueOf(sex)));
		try{
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = httpResponse.getEntity();
				if( entity != null){
					String result = readInputStream(entity.getContent());
					int iResult = Integer.valueOf(result);
					return iResult;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			
		}
		return 0;
	}

	/**
	 * 
	 * @param username
	 * @param authCode
	 * @return
	 */
	public String auth(String username, String authCode) {
		// TODO Auto-generated method stub
		HttpPost httpRequest = new HttpPost(Strings.AUTH_URL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("authcode", authCode));
		try{
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = httpResponse.getEntity();
				if(entity != null){
					String result = readInputStream(entity.getContent());
					return result;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

    /**
     *用户注册时，请求获取验证码
     * @param phone
     * @return int：0-已注册；1-请求成功；2-发生错误
     */
    public int getTheCode(String phone){
        HttpPost httpPost = new HttpPost(Strings.GET_THE_CODE);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        BasicNameValuePair param = new BasicNameValuePair("phone", phone);
        params.add(param);
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    String result = readInputStream(entity.getContent());
                    if(result != null && result.length() >= 1) return Integer.valueOf(result);
                    else return 2;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return 2;
    }
	/**
	 * if publish success, the server return ID(ID > 0)
	 * if ID=0,means failed
	 * @param content
	 * @param location
	 * @param number
	 * @param contactNumber
	 * @return
	 */
	public static String publish(String username, String content, String location, int number,
			String contactNumber) {
		// TODO Auto-generated method stub
		HttpPost httpRequest = new HttpPost(Strings.PUBLISH_URL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("content", content));
		params.add(new BasicNameValuePair("location", location));
		params.add(new BasicNameValuePair("number", String.valueOf(number)));
		params.add(new BasicNameValuePair("contactNumber", contactNumber));
		try{
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = httpResponse.getEntity();
				if(entity != null){
					String result = readInputStream(entity.getContent());
					return result;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static String acceptMission(String username, long id) {
		// TODO Auto-generated method stub
		HttpPost httpRequest = new HttpPost(Strings.ACCEPT_MISSION_URL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("ID", String.valueOf(id)));
		try{
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = httpResponse.getEntity();
				if(entity != null){
					String result = readInputStream(entity.getContent());
					return result;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

    /**
     *
     * @param strPhone
     */
    public static String getCheckNumber(String strPhone) {
        return null;
    }

    /**
     *
     * @param strPhone
     */
    public static void sendCheckNumber(String strPhone) {

    }

    public static Boolean upLoadErrorQuestion(String json){
        HttpPost httpPost = new HttpPost(Strings.URL_UPLOAD_PAPER_INFO);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("questions", json));
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    String result = readInputStream(entity.getContent());
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean upLoadPaperInfo(Paper paper, String username){
        if(paper == null )return false;
            String strPaper = FileOperation.parsePaperReport2Str(paper);
            HttpPost httpPost = new HttpPost(Strings.URL_UPLOAD_PAPER_INFO);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("paperReport",strPaper));
            try{
                httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);

                if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    HttpEntity entity = httpResponse.getEntity();
                    if(entity != null){
                        String result = readInputStream(entity.getContent());
                        if(result != null && result.equals("1"))return true;
                        else return false;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        return false;
    }

    public static Boolean upLoadPaperInfo(String strPaper, String username){
        if(strPaper == null || strPaper.length() <= 0)return false;
        HttpPost httpPost = new HttpPost(Strings.URL_UPLOAD_PAPER_INFO);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("paperReport",strPaper));
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);

            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    String result = readInputStream(entity.getContent());
                    if(result != null && result.equals("1"))return true;
                    else return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static ArrayList<Question> getUserQuestionInfo(String username){
        HttpPost httpPost = new HttpPost(Strings.URL_GET_USER_QUESTION_INFO);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    String result = readInputStream(entity.getContent());
                    if(result == null || result.length() < 1)return null;
                    JSONObject jsonObject = new JSONObject(result);
                    return FileOperation.parseUserQuestionInfo(jsonObject);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Paper> getZhenTiPapers(String username, int courseID){
        HttpPost httpPost = new HttpPost(Strings.URL_ZHEN_TI);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("courseID", String.valueOf(courseID)));
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    String result = readInputStream(entity.getContent());
                    if(result == null || result.length() < 1)return null;
                    JSONObject jsonObject = new JSONObject(result);
                    return FileOperation.parsePapers(jsonObject);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param username
     * @param courseID
     * @return ArrayList<Paper>
     */
    public static ArrayList<Paper> getIndividuationPaper(String username, int courseID){
        HttpPost httpPost = new HttpPost(Strings.URL_INDIVIDUATION);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("courseID", String.valueOf(courseID)));
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    String result = readInputStream(entity.getContent());
                    if(result == null || result.length() < 1)return null;
                    JSONObject jsonObject = new JSONObject(result);
                    return FileOperation.parsePapers(jsonObject);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static AssessmentScore getAssessmentScore(String username, int courseID){
        HttpPost httpPost = new HttpPost(Strings.URL_GET_USER_SCORE);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("courseID", String.valueOf(courseID)));
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    String result = readInputStream(entity.getContent());
                    if(result == null || result.length() <= 2)return null;
                    JSONObject jsonObject = new JSONObject(result);
                    return FileOperation.parseAssessmentScore(jsonObject);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Paper> getFinishedPapers(String username, int courseID ){
//        ArrayList<Paper> papers = null;
        HttpPost httpPost = new HttpPost(Strings.URL_GET_FINISHED_PAPER);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("courseID", String.valueOf(courseID)));
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    String result = readInputStream(entity.getContent());
                    if(result == null || result.length() <= 1)return null;
                    JSONObject jsonObject = new JSONObject(result);
                    return FileOperation.parseFinishedPapers(jsonObject);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean uploadUserIcon(String username, String password, String strIcon){
        HttpPost httpPost = new HttpPost(Strings.URL_UPLOAD_USER_ICON);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("strICON", strIcon));
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    String result = readInputStream(entity.getContent());
                    if(result == null || result.length() < 1)return false;
                    int iResult = Integer.valueOf(result);
                    if(iResult == 1)return true;
                    else if(iResult == -1)return false;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static String getUserIcon(String username, String password){
        HttpPost httpPost = new HttpPost(Strings.URL_GET_USER_ICON);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    String result = readInputStream(entity.getContent());
                    if(result == null || result.length() <= 1)return null;
                    return result;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
