import axios from "axios";

export const getToken = () => localStorage.getItem("jwtToken");

const apiWithCredentials = axios.create({
  headers: {
    Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
  },
});

apiWithCredentials.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  } else {
    delete config.headers.Authorization;
  }
  return config;
});

const basePost = (endpointName, success) => {
  apiWithCredentials
    .post(endpointName)
    .then((resp) => success(resp.data))
    .catch((error) =>
      console.error("There was a problem with the fetch operation:", error)
    );
};

const basePostForm = (endpointName, body, success) => {
  apiWithCredentials
    .post(endpointName, body)
    .then((resp) => success(resp.data))
    .catch((error) =>
      console.error("There was a problem with the fetch operation:", error)
    );
};

const basePatch = (endpointName, body, success) => {
  apiWithCredentials
    .patch(endpointName, body)
    .then((resp) => success(resp.data))
    .catch((error) =>
      console.error("There was a problem with the fetch operation:", error)
    );
};

const baseGet = (endpointName, success) => {
  apiWithCredentials
    .get(endpointName)
    .then((resp) => success(resp.data))
    .catch((error) =>
      console.error("There was a problem with the fetch operation:", error)
    );
};

const baseDelete = (endpointName, success) => {
  apiWithCredentials
    .delete(endpointName)
    .then((resp) => success(resp.data))
    .catch((error) =>
      console.error("There was a problem with the fetch operation:", error)
    );
};

//USER
export const login = async (username, password, success) =>
  axios
    .post("/api/v1/users/login", { username, password })
    .then((resp) => {
      const { token } = resp.data;
      localStorage.setItem("jwtToken", token);
      if (success) success(token);
    })
    .catch((error) => {
      console.error("Login failed:", error);
    });

export const logout = () => localStorage.removeItem("jwtToken");

export const getRecommendedSongs = async (pageNumber, success) =>
  baseGet(
    "/api/v1/users/recommended?pageNumber=" + pageNumber + "&pageSize=10",
    success
  );

export const getUserData = async (success) => baseGet("/api/v1/users", success);

export const getUser = async (userId, success) =>
  baseGet("/api/v1/users/" + userId, success);

export const getCurrentUser = async (success) =>
  baseGet("/api/v1/users", success);

export const searchUser = async (pageNumber, userName, success) =>
  baseGet(
    "/api/v1/users/search/" +
      userName +
      "?pageNumber=" +
      pageNumber +
      "&pageSize=10",
    success
  );

export const followUser = async (userId, success) =>
  basePost("/api/v1/users/follow?userId=" + userId, success);

export const register = async (body, success) =>
  basePostForm("/api/v1/users/add", new URLSearchParams(body), success);

//ALBUMS
export const getAlbums = async (success) => baseGet("/api/v1/albums", success);

export const getAlbum = async (albumId, success) =>
  baseGet("/api/v1/albums/" + albumId, success);

export const searchAlbums = async (pageNumber, albumName, success) =>
  baseGet(
    "/api/v1/albums/search/" +
      albumName +
      "?pageNumber=" +
      pageNumber +
      "&pageSize=10",
    success
  );

export const removeAlbum = async (albumId, success) =>
  baseDelete("/api/v1/albums/" + albumId, success);

export const patchAlbum = async (albumId, body, success) =>
  basePatch("/api/v1/albums/" + albumId, body, success);

export const addAlbum = async (body, success) =>
  basePostForm("/api/v1/albums", body, success);

export const getRecommendedSongsForAlbum = async (
  pageNumber,
  albumId,
  success
) =>
  baseGet(
    "/api/v1/albums/recommended/" +
      albumId +
      "?pageNumber=" +
      pageNumber +
      "&pageSize=10",
    success
  );

//TAGS
export const getTags = async (tagName, success) =>
  baseGet("/api/v1/tags/search/" + tagName, success);

//SONGS
export const getSongs = async (success) => baseGet("/api/v1/songs", success);

export const getSong = async (songId, success) =>
  baseGet("/api/v1/songs/" + songId, success);

export const searchSongs = async (pageNumber, songName, success) =>
  baseGet(
    "/api/v1/songs/search/" +
      songName +
      "?pageNumber=" +
      pageNumber +
      "&pageSize=10",
    success
  );

export const removeSong = async (songId, success) =>
  baseDelete("/api/v1/songs/" + songId, success);

export const patchSong = async (songId, body, success) =>
  basePatch("/api/v1/songs/" + songId, body, success);

export const addSong = async (body, success) =>
  basePostForm("/api/v1/songs", body, success);

export const getRecommendedSongsForSong = async (pageNumber, songId, success) =>
  baseGet(
    "/api/v1/songs/recommended/" +
      songId +
      "?pageNumber=" +
      pageNumber +
      "&pageSize=10",
    success
  );

//FILES
export const getMusic = async (nameHashed, success) =>
  baseGet("/api/v1/files/music/" + nameHashed, success);
