#!/usr/bin/env python3
"""
Downloads the full TheCocktailDB dataset (free dev key "1") and pre-processes
it into a clean, lean JSON format used by the Barnee app.

Fields kept:
  id, alias, name, imageUrl, ingredients, steps, categories, tags, ibaCategory

Fields dropped (not used by the app):
  - strInstructionsES/DE/FR/IT/ZH-HANS/ZH-HANT  (non-English instructions)
  - strDrinkAlternate                             (alternate name, unused)
  - strVideo                                      (always null in free dataset)
  - strCreativeCommonsConfirmed, strImageAttribution, strImageSource, dateModified
  - raw strIngredient1..15 / strMeasure1..15      (replaced by ingredients list)
  - raw strTags string                            (replaced by tags list)

Run:
  python3 scripts/download_cocktails.py

Output:
  shared/src/commonMain/resources/cocktails.json
"""

import json
import re
import time
import urllib.request
from pathlib import Path

OUTPUT = Path(__file__).parent.parent / "shared/src/commonMain/resources/cocktails.json"
API_BASE = "https://www.thecocktaildb.com/api/json/v1/1/search.php?f="


def slugify(name: str) -> str:
    return re.sub(r"-{2,}", "-", re.sub(r"[^a-z0-9-]", "", name.lower().replace(" ", "-"))).strip("-")


def get_ingredients(drink: dict) -> list[dict]:
    result = []
    for i in range(1, 16):
        ingredient = (drink.get(f"strIngredient{i}") or "").strip()
        measure = (drink.get(f"strMeasure{i}") or "").strip()
        if not ingredient:
            break
        text = f"{measure} {ingredient}".strip() if measure else ingredient
        result.append({"text": text})
    return result


def get_steps(instructions: str | None) -> list[str]:
    if not instructions:
        return []
    # Split on sentence boundaries, keep meaningful steps
    raw = re.split(r"(?<=[.!?])\s+", instructions.strip())
    return [s.rstrip(".").strip() for s in raw if s.strip()]


def fetch_all_drinks() -> list[dict]:
    all_drinks: list[dict] = []
    seen_ids: set[str] = set()

    for letter in "abcdefghijklmnopqrstuvwxyz":
        url = f"{API_BASE}{letter}"
        try:
            with urllib.request.urlopen(url, timeout=10) as resp:
                data = json.loads(resp.read())
                drinks = data.get("drinks") or []
                for d in drinks:
                    if d["idDrink"] not in seen_ids:
                        seen_ids.add(d["idDrink"])
                        all_drinks.append(d)
                print(f"  {letter}: {len(drinks)} drinks")
        except Exception as e:
            print(f"  {letter}: error — {e}")
        time.sleep(0.25)

    return all_drinks


def process_drinks(raw_drinks: list[dict]) -> list[dict]:
    seen_aliases: dict[str, str] = {}  # alias -> idDrink
    result = []

    for d in raw_drinks:
        drink_id = d["idDrink"]
        name = d.get("strDrink") or ""
        base_alias = slugify(name)

        # Resolve duplicate aliases by appending the drink id
        if base_alias in seen_aliases and seen_aliases[base_alias] != drink_id:
            alias = f"{base_alias}-{drink_id}"
        else:
            alias = base_alias
        seen_aliases[alias] = drink_id

        categories = []
        for field in ("strCategory", "strGlass", "strAlcoholic"):
            val = (d.get(field) or "").strip()
            if val:
                categories.append(val)

        tags = [t.strip() for t in (d.get("strTags") or "").split(",") if t.strip()]

        result.append({
            "id": drink_id,
            "alias": alias,
            "name": name,
            "imageUrl": d.get("strDrinkThumb") or None,
            "ingredients": get_ingredients(d),
            "steps": get_steps(d.get("strInstructions")),
            "categories": categories,
            "tags": tags,
            "ibaCategory": (d.get("strIBA") or "").strip() or None,
        })

    return result


def main():
    print("Fetching drinks from TheCocktailDB...")
    raw = fetch_all_drinks()
    print(f"Downloaded {len(raw)} drinks total")

    print("Processing...")
    clean = process_drinks(raw)

    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    with open(OUTPUT, "w", encoding="utf-8") as f:
        json.dump(clean, f, indent=2, ensure_ascii=False)

    size_kb = OUTPUT.stat().st_size // 1024
    print(f"Written {len(clean)} drinks to {OUTPUT} ({size_kb} KB)")


if __name__ == "__main__":
    main()
