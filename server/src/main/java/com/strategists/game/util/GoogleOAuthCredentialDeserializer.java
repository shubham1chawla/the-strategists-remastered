package com.strategists.game.util;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import com.strategists.game.request.GoogleOAuthCredential;

import lombok.val;

public class GoogleOAuthCredentialDeserializer extends StdDeserializer<GoogleOAuthCredential> {

	private static final long serialVersionUID = 3181496884500371979L;

	private static final String CREDENTIAL_FIELD = "credential";
	private static final String NAME_FIELD = "name";
	private static final String EMAIL_FIELD = "email";

	public GoogleOAuthCredentialDeserializer() {
		super(GoogleOAuthCredential.class);
	}

	@Override
	public GoogleOAuthCredential deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		val node = p.getCodec().readTree(p);
		Assert.isTrue(node.isObject(), "JSON request is not object!");

		val tokenNode = node.get(CREDENTIAL_FIELD);
		Assert.notNull(tokenNode, "Token field not present in JSON!");

		val token = ((TextNode) tokenNode).asText();
		Assert.hasText(token, "Google OAuth token can't be empty!");

		val parts = token.split("\\.");
		val mapper = new ObjectMapper();
		val map = mapper.readValue(Base64.getDecoder().decode(parts[1]), HashMap.class);

		return new GoogleOAuthCredential((String) map.get(NAME_FIELD), (String) map.get(EMAIL_FIELD));
	}

}
