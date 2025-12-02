export const AvailabilityEnum = Object.freeze({
  AVAILABLE: "Available",
  CHECKED_OUT: "Checked Out",
  ON_HOLD: "On Hold",
});

export function getDisplayStr(enumKey) {
  return AvailabilityEnum[enumKey];
}

export function getAvailabilityEnumFromStr(displayStr) {
  const entry = Object.entries(AvailabilityEnum).find(
    ([_, v]) => v === displayStr
  );
  return entry ? entry[0] : null;
}
