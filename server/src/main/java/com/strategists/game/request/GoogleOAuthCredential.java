package com.strategists.game.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strategists.game.util.GoogleOAuthCredentialDeserializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = GoogleOAuthCredentialDeserializer.class)
public class GoogleOAuthCredential {

	private static final String JSON_FORMAT = "{\"credential\":\"%s\"}";

	private String name;
	private String email;

	public static GoogleOAuthCredential fromJWT(String jwtString) throws JsonProcessingException {
		val mapper = new ObjectMapper();
		return mapper.readValue(String.format(JSON_FORMAT, jwtString), GoogleOAuthCredential.class);
	}

}
