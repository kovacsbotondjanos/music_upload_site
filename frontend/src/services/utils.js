const detailed = {
  year: "numeric",
  month: "long",
  day: "numeric",
  hour: "2-digit",
  minute: "2-digit",
  second: "2-digit",
};

const regular = {
  year: "numeric",
  month: "long",
  day: "numeric",
};

export const formatStringToDate = (dateString) =>
  new Date(dateString).toLocaleString("en-US", regular);

export const formatStringToDateDetailed = (dateString) =>
  new Date(dateString).toLocaleString("en-US", detailed);
