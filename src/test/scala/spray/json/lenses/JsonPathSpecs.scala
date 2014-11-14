package spray.json
package lenses

import org.specs2.mutable.Specification

class JsonPathSpecs extends Specification {

  import JsonPath._

  "JsonPath parser" should {
    "simple field selection" in {
      parse("$.test") must be_==(Selection(Root, ByField("test")))
    }
    "multiple field selection" in {
      parse("$.test.test2") must be_==(Selection(Selection(Root, ByField("test")), ByField("test2")))
    }
    "wildcard selection" in {
      parse("$.a[*].c") must be_==(Selection(Selection(Selection(Root, ByField("a")), AllElements), ByField("c")))
    }
    "element selection" in {
      "of root" in {
        parse("$[2]") must be_==(Selection(Root, ByIndex(2)))
      }
      "of field" in {
        parse("$['abc']") must be_==(Selection(Root, ByField("abc")))
      }

      "by predicate: eq" in {
        parse("$[?(@.id == 'test')]") must be_==(Selection(Root, ByPredicate(Eq(PathExpr(Selection(Root, ByField("id"))), Constant(JsString("test"))))))
      }

      "by predicate: lt" in {
        parse("$[?(@.id < 12)]") must be_==(Selection(Root, ByPredicate(Lt(PathExpr(Selection(Root, ByField("id"))), Constant(JsNumber(12))))))
      }

      "by predicate: exists" in {
        parse("$[?(@.id)]") must be_==(Selection(Root, ByPredicate(Exists(Selection(Root, ByField("id"))))))
      }

      "by predicate: strings with spaces in conditions" in {
        parse("$[?(@.title=='The Space Merchants')]") must be_==(
          Selection(Root, ByPredicate(Eq(PathExpr(Selection(Root, ByField("title"))), Constant(JsString("The Space Merchants"))))))
      }
    }
  }

  def parse(str: String) = JsonPathParser(str)
}
