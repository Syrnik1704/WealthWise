# Uruchamianie aplikacji z Dockerem

## Wymagania

- Zainstalowany Docker (sprawdź [oficjalną stronę](https://docs.docker.com/get-docker/), aby dowiedzieć się, jak zainstalować Dockera na swoim systemie).
- Docker uruchomiony na Twoim komputerze.

## Kroki do uruchomienia aplikacji

1. Upewnij się, że Docker jest zainstalowany i uruchomiony na Twoim systemie.

   Możesz sprawdzić status Dockera za pomocą komendy:
      ```bash
    docker ps
      ```

2. Przejdź do katalogu, w którym znajduje się plik `docker-compose.yml`:

3. Aby uruchomić aplikację, użyj następującej komendy:
    ```bash
    docker-compose up
   ```
   Docker zbuduje obrazy, jeśli nie zostały jeszcze zbudowane i uruchomi kontenery.

4. Jeśli dokonałeś jakichkolwiek zmian w plikach projektu lub konfiguracji, musisz przebudować obrazy Dockera. Aby to zrobić, użyj komendy:
    ```bash
    docker-compose build
   ```
5. Po przebudowie, ponownie uruchom aplikację za pomocą komendy:
    ```bash
    docker-compose up 
   ```

## Wymagania
 Dodanie opcji -d do docker-compose up uruchomi proces w trybie detach (proces bedzie działał w tle)

## Requesty do postmana 
/auth/register (POST)
body:
```bash
{
    "name": "Jan",
    "surname": "Kowalski",
    "birthDay": "29-05-2000",
    "email": "example12@test.com",
    "password": "Password1",
    "confirmPassword": "Password1"
}
```

/auth/login (POST)
body:
```bash
{
    "email": "example12@test.com",
    "password": "Password1"
}
```

 
