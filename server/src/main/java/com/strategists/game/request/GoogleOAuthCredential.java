package com.strategists.game.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strategists.game.util.GoogleOAuthCredentialDeserializer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = GoogleOAuthCredentialDeserializer.class)
public class GoogleOAuthCredential {

	private String name;
	private String email;

}
