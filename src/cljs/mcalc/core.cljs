(ns mcalc.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [mcalc.views :as views]))

(defn calc-payments
  []
  (if (> calcPeriods 0)
    (loop [loanAmount loanAmount
           acc []
           index 1]
      (let [interest (* loanAmount loanMonthRate)
            principal (- loanPayment interest)
            taxDeduct (+ interest pmiMonthly propertyTax)
            taxAdvantage (if (> taxDeduct standardTaxDeduction)
                           (* (- taxDeduct standardTaxDeduction) incomeTaxRate 0.01)
                           0)
            pmiMonthly (if (<= loanAmount (* 0.8 fullPrice)) 0 pmiMonthly)
            payment (+ loanPayment pmiMonthly homeInsurance propertyTax hoa)
            data {:monthN index
                  :principal principal
                  :interest interest
                  :pmi pmiMonthly
                  :taxDeduction taxDeduct
                  :taxAdvantage taxAdvantage
                  :payment payment
                  :adjustedPayment (- payment taxAdvantage)
                  :wastedMoney (- payment taxAdvantage principal)}

            acc (conj acc data)]
        (if (= index calcPeriods)
          acc
          (recur (- loanAmount principal)
            acc
            (inc index)))))))

(defn calculate
  [{:keys [fullPrice downPayment loanYears loanRate
           pmiRate propertyTaxRate incomeTaxRate standardTaxDeduction
           hoa homeInsurance calcPeriods]}]
  (let [loanAmount (- fullPrice downPayment)
        periods (* 12 loanYears)
        loanMonthRate (/ loanRate 12 100)
        standardTaxDeduction (/ standardTaxDeduction 12)
        loanPayment (/ loanAmount
                      (/ (- 1 (Math/pow (+ 1 loanMonthRate) (- periods)))
                        loanMonthRate))
        propertyTax (* fullPrice (/ propertyTaxRate 12 100))
        pmiMonthly (* fullPrice (/ pmiRate 12 100))
        calcPeriods (if (> calcPeriods periods) periods calcPeriods)]

    {:loanAmount loanAmount
     :loanMonthRate loanMonthRate
     :loanPayment loanPayment
     :propertyTax propertyTax
     :payments}))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (mount-root))
