package spray.json
package lenses

trait ExtraImplicits {
  trait RichJsValue {
    def value: JsValue

    def update(updater: GeneralUpdate[JsValue]): JsValue = updater(value)

    def update[T: JsonWriter, M[_]](lens: GeneralUpdateLens[JsValue, JsValue], pValue: T): JsValue =
      lens ! Operations.set(pValue) apply value

    // This can't be simplified because we don't want the type constructor
    // for Lens[M] to appear in the type parameter list.
    def extract[T: Reader](p: Lens[Id]): T =
      p.get[T](value)

    def extract[T: Reader](p: Lens[Option]): Option[T] =
      p.get[T](value)

    def extract[T: Reader](p: Lens[Seq]): Seq[T] =
      p.get[T](value)

    def as[T: Reader]: Validated[T] =
      implicitly[Reader[T]].read(value)
  }

  implicit def richValue(v: JsValue): RichJsValue = new RichJsValue { def value = v }
  implicit def richString(str: String): RichJsValue = new RichJsValue { def value = JsonParser(str) }
}

object ExtraImplicits extends ExtraImplicits
