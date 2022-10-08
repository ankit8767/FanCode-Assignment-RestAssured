package tests;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

import java.util.*;

import org.json.JSONArray;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class TestFeature {
	private final String BASE_URI = "http://jsonplaceholder.typicode.com";
	public static ArrayList<Integer> listOfUsersFromDreamCity = new ArrayList<Integer>(); 
	
	@BeforeTest
	public void preCondition() {
		RestAssured.baseURI = BASE_URI;
	}
	
	@Test
	public void getUsersFromFancodeCity() {
		Response response = given().
                when().
                get("/users");
		assertEquals(200, response.getStatusCode());
		
		User[] users = response.jsonPath().getObject("$", User[].class);
		//Adding users belonging to dream city 
		for(User user : users) {
			float lat = Float.valueOf(user.address.geo.getLat());
			float lng = Float.valueOf(user.address.geo.getLng());
			if((lat>=-40 && lat<=5) && (lng>=5 && lng<=100)) {
				listOfUsersFromDreamCity.add(user.getId());
			}
		}
		
	}
	
	@Test(dependsOnMethods = "getUsersFromFancodeCity")
	public void checkIfTodosAreCompletedMoreThanHalfForDreamCityUsers() {
		for(int i = 0; i < listOfUsersFromDreamCity.size();i++) {
			Response response = given().
					param("userId",listOfUsersFromDreamCity.get(i)).
	                when().
	                get("/todos");
			assertEquals(200, response.getStatusCode());
			Todos[] todos = response.jsonPath().getObject("$", Todos[].class);
			int totalTodos = todos.length;
			Response responseCompletedTodos = given().
					param("userId",listOfUsersFromDreamCity.get(i)).
					param("completed","true").
	                when().
	                get("/todos");
			Todos[] todosCompleted = responseCompletedTodos.jsonPath().getObject("$", Todos[].class);
			int totalCompletedTodos = todosCompleted.length;
			assert totalCompletedTodos>totalTodos/2;
			
		}
		
	}
}
