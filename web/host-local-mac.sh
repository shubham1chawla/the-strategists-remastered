# Getting IP Address of MAC
IP_ADDR=$(ipconfig getifaddr en0)

# Hosting React app
bash -c "HOST=${IP_ADDR} REACT_APP_API_BASE_URL=http://${IP_ADDR}:8090 npm run start"